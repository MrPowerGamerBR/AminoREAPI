package net.perfectdreams.aminoreapi

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.kevinsawicki.http.HttpRequest
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import io.ktor.client.request.header
import mu.KotlinLogging
import net.perfectdreams.aminoreapi.entities.*
import net.perfectdreams.aminoreapi.hooks.EventListener
import net.perfectdreams.aminoreapi.utils.Endpoints
import net.perfectdreams.aminoreapi.utils.IncorrentLoginException
import net.perfectdreams.aminoreapi.utils.InvalidPasswordException
import net.perfectdreams.aminoreapi.utils.MiscUtils
import net.perfectdreams.aminoreapi.websocket.AminoWebSocket
import java.io.File
import java.net.URLEncoder
import java.util.*
import java.util.concurrent.TimeUnit

class AminoClient(
		private val credentials: AminoClientBuilder.Credentials,
		private val deviceId: String,
		private var connectToWebSocket: Boolean,
		private var cacheEnabled: Boolean
) {
	companion object {
		private val logger = KotlinLogging.logger {}
	}

	private var sessionId: String? = null
	internal var listeners = mutableListOf<EventListener>()
	private val http = HttpClient(OkHttp) {
		expectSuccess = false
	}
	private var webSocket: AminoWebSocket? = null
	private val communityCache = Caffeine.newBuilder()
			.expireAfterWrite(1L, TimeUnit.MINUTES)
			.maximumSize(if (cacheEnabled) Long.MAX_VALUE else 0)
			.build<Long, Community>()
			.asMap()
	private val threadCache = Caffeine.newBuilder()
			.expireAfterWrite(1L, TimeUnit.MINUTES)
			.maximumSize(if (cacheEnabled) Long.MAX_VALUE else 0)
			.build<UUID, Thread>()
			.asMap()

	suspend fun login() {
		// required for wsX.narvii.com
		System.setProperty("https.protocols", "TLSv1.1")

		if (credentials is AminoClientBuilder.SessionIdCredentials)
			this.sessionId = credentials.sessionId
		else {
			val payload = JsonObject()

			if (credentials is AminoClientBuilder.EmailAndPasswordCredentials) {
				payload["email"] = credentials.email
				payload["secret"] = "0 ${credentials.password}" // TODO: Always 0?
			}

			payload["deviceID"] = deviceId
			payload["clientType"] = 100 // TODO: Always 100?
			payload["action"] = "normal"
			payload["timestamp"] = System.currentTimeMillis()

			val body = HttpRequest.post(Endpoints.LOGIN)
					.header("NDCDEVICEID", deviceId)
					.header("NDC-MSG-SIG", getMessageSignature())
					.send(payload.toString())
					.body()

			val response = jsonParser.parse(body).obj
			val statusCode = response["api:statuscode"].int

			if (statusCode == 214)
				throw InvalidPasswordException()

			if (statusCode == 200)
				throw IncorrentLoginException()

			val sid = response["sid"].nullString

			_println(response)

			if (sid == null) {
				return
			}

			sessionId = sid
		}

		connectToWebSocket()
	}

	fun getCachedCommunities() = Collections.unmodifiableCollection(communityCache.values)
	fun getCachedThreads() = Collections.unmodifiableCollection(threadCache.values)

	fun sendDeviceInfo(info: DeviceInfo) {
		val body = HttpRequest.post(Endpoints.DEVICE_INFO)
				.header("NDCDEVICEID", deviceId)
				.header("NDC-MSG-SIG", getMessageSignature())
				.send(gson.toJson(info))
				.body()

		_println(body)
	}

	fun getCommunityCollectionSections(languageCode: String, start: Int, size: Int) {
		val body = HttpRequest.get(Endpoints.COMMUNITY_COLLECTION_SECTIONS.format(languageCode, start, size))
				.header("NDCDEVICEID", deviceId)
				.header("NDCAUTH", "sid=$sessionId")
				.body()

		_println(body)
	}

	suspend fun getJoinedCommunities(start: Int, size: Int): List<Community> {
		val body = get(Endpoints.JOINED_COMMUNITIES.format(start, size))

		_println(body)

		val payload = jsonParser.parse(body)
		val communityList = payload["communityList"].array
		val userInfoInCommunities = payload["userInfoInCommunities"].obj

		return gson.fromJson<List<Community>>(communityList).onEach {
			it.client = this
			communityCache[it.ndcId] = it
			userInfoInCommunities[it.ndcId.toString()].nullObj?.let { jsonObject ->
				it.userInfo = gson.fromJson(jsonObject)
			}
		}
	}

	fun getCommunityById(id: String) = getCommunityById(MiscUtils.toLongCommunityId(id))
	fun getCommunityById(id: Long) = communityCache[id]

	suspend fun retrieveCommunityById(id: String, checkOnCache: Boolean = true) = retrieveCommunityById(MiscUtils.toLongCommunityId(id), checkOnCache)

	suspend fun retrieveCommunityById(id: Long, checkOnCache: Boolean = true): Community? {
		if (checkOnCache)
			communityCache[id]?.let { return it }

		val body = get(Endpoints.COMMUNITY_INFO.format(id))

		_println(body)

		val payload = jsonParser.parse(body)
		val community = payload["community"].obj
		val userInfoInCommunity = payload["currentUserInfo"].obj

		val daoCommunity = gson.fromJson<Community>(community).also { it.client = this; it.userInfo = gson.fromJson(userInfoInCommunity) }
		communityCache[id] = daoCommunity

		return daoCommunity
	}

	fun getThreadById(communityId: String, id: UUID) = getThreadById(MiscUtils.toLongCommunityId(communityId), id)
	fun getThreadById(communityId: Long, id: UUID) = threadCache[id]

	suspend fun retrieveThreadById(communityId: String, id: UUID, checkOnCache: Boolean = true) = retrieveThreadById(MiscUtils.toLongCommunityId(communityId), id)

	suspend fun retrieveThreadById(communityId: Long, id: UUID, checkOnCache: Boolean = true): Thread? {
		if (threadCache.containsKey(id))
			return threadCache[id]

		val body = get(Endpoints.COMMUNITY_THREAD.format(communityId, id))

		_println(body)
		val community = retrieveCommunityById(communityId) ?: throw IllegalStateException("Getting thread information from a non-existent community!")

		val thread = gson.fromJson<Thread>(jsonParser.parse(body)["thread"]).also {
			it.community = community
			it.client = this
		}

		threadCache[id] = thread

		return thread
	}

	fun getThreadMessages(ndcId: String, threadId: String, start: Int, size: Int, startTime: String? = null): List<AminoMessage> {
		val url = if (startTime != null) {
			Endpoints.COMMUNITY_CHAT_GET_MESSAGES_SINCE.format(ndcId, threadId, start, size, startTime)
		} else {
			Endpoints.COMMUNITY_CHAT_GET_MESSAGES.format(ndcId, threadId, start, size)
		}

		val body = HttpRequest.get(url)
				.header("NDCDEVICEID", deviceId)
				.header("NDCAUTH", "sid=$sessionId")
				.body()

		_println(body)
		return gson.fromJson(jsonParser.parse(body)["messageList"])
	}

	fun sendMessageInThread(ndcId: String, threadId: String, content: String, clientRefId: Long = System.currentTimeMillis() / 1000): AminoMessage {
		val payload = JsonObject()

		payload["content"] = content
		payload["type"] = 0
		payload["clientRefId"] = clientRefId
		payload["timestamp"] = System.currentTimeMillis() / 1000

		val body = HttpRequest.post(Endpoints.COMMUNITY_CHAT_SEND_MESSAGE.format(ndcId, threadId))
				.header("NDCDEVICEID", deviceId)
				.header("NDCAUTH", "sid=$sessionId")
				.send(payload.toString())
				.body()

		_println(body)

		return gson.fromJson(jsonParser.parse(body)["message"])
	}

	fun sendFileInThread(ndcId: String, threadId: String, file: File, clientRefId: Long = System.currentTimeMillis() / 1000): AminoMessage {
		val payload = JsonObject()

		payload["content"] = null
		payload["type"] = 0
		payload["mediaType"] = 100
		// payload["mediaUploadValue"] = DatatypeConverter.printBase64Binary(file.readBytes())
		payload["clientRefId"] = clientRefId
		payload["timestamp"] = System.currentTimeMillis() / 1000

		val body = HttpRequest.post(Endpoints.COMMUNITY_CHAT_SEND_MESSAGE.format(ndcId, threadId))
				.header("NDCDEVICEID", deviceId)
				.header("NDCAUTH", "sid=$sessionId")
				.send(payload.toString())
				.body()

		_println(body)

		return gson.fromJson(jsonParser.parse(body)["message"])
	}

	fun checkReminders(timezone: Int, vararg ndcIds: String): Map<String, AminoReminders> {
		val batches = mutableListOf<List<String>>()

		var currentList = mutableListOf<String>()
		batches.add(currentList)
		for (ndcId in ndcIds) {
			if (currentList.size == 10) {
				currentList = mutableListOf()
				batches.add(currentList)
			}
			currentList.add(ndcId)
		}

		val map = mutableMapOf<String, AminoReminders>()

		for (batch in batches) {
			val body = HttpRequest.get(Endpoints.REMINDERS.format(URLEncoder.encode(batch.joinToString(","), "UTF-8"), timezone))
					.header("NDCDEVICEID", deviceId)
					.header("NDCAUTH", "sid=$sessionId")
					.body()

			_println(body)

			jsonParser.parse(body)["reminderCheckResultInCommunities"].obj.entrySet().forEach {
				map[it.key] = gson.fromJson(it.value)
			}
		}

		return map
	}

	fun checkIn(timezone: Int, ndcId: String): CheckInResponse {
		val payload = JsonObject()
		payload["timezone"] = timezone
		payload["ndcId"] = ndcId

		val body = HttpRequest.post(Endpoints.COMMUNITY_CHECK_IN.format(ndcId))
				.header("NDCDEVICEID", deviceId)
				.header("NDCAUTH", "sid=$sessionId")
				.header("NDC-MSG-SIG", getMessageSignature())
				.send(payload.toString())
				.body()

		return gson.fromJson(body)
	}

	fun getPublicChats(ndcId: String, start: Int, size: Int): PublicChats {
		val body = HttpRequest.get(Endpoints.LIVE_LAYERS_PUBLIC_CHAT.format(ndcId, start, size))
				.header("NDCDEVICEID", deviceId)
				.header("NDCAUTH", "sid=$sessionId")
				.body()

		_println(body)

		return gson.fromJson(body)
	}

	fun getBlogFeed(ndcId: String, start: Int, size: Int) {
		val body = HttpRequest.get(Endpoints.COMMUNITY_FEED.format(ndcId, start, size))
				.header("NDCDEVICEID", deviceId)
				.header("NDCAUTH", "sid=$sessionId")
				.body()

		_println(body)

		return gson.fromJson(body)
	}

	suspend fun get(url: String, headers: Map<String, String> = mutableMapOf("NDCDEVICEID" to deviceId, "NDCAUTH" to "sid=$sessionId"), vararg variables: Any): String {
		val result = http.get<String>(url) {
			headers.forEach { key, value ->
				header(key, value)
			}
		}

		return result
	}

	fun post(url: String, headers: Map<String, String> = mutableMapOf("NDCDEVICEID" to deviceId, "NDCAUTH" to "sid=$sessionId"), payload: JsonObject): String {
		return post(url, headers, gson.toJson(payload))
	}

	fun post(url: String, headers: Map<String, String> = mutableMapOf("NDCDEVICEID" to deviceId, "NDCAUTH" to "sid=$sessionId"), payload: String): String {
		_println("payload:")
		_println(payload)

		println(url)

		val body = HttpRequest.post(url)
				.headers(headers)
				.send(payload)
				.body()

		_println(body)

		return body
	}

	fun addEventListener(eventListener: EventListener) {
		listeners.add(eventListener)
	}

	suspend fun getWebSocketUrl(): String {
		/* val payload = http.get<String>("https://aminoapps.com/api/chat/web-socket-url") {
			this.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/73.0.3683.86 Chrome/73.0.3683.86 Safari/537.36")
			// this.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/73.0.3683.86 Chrome/73.0.3683.86 Safari/537.36")
			this.header("X-Request-With", "XMLHttpRequest")
			this.header("Cookie", "device_id=$deviceId; sid=$sessionId")
		} */

		// return json["result"]["url"].string
		return "wss://ws1.narvii.com/?signbody=${deviceId}%7C${System.currentTimeMillis()}"
	}

	suspend fun connectToWebSocket() {
		logger.info("Connecting to WebSocket...")

		val webSocket = AminoWebSocket(
				this,
				getWebSocketUrl(),
				mapOf(
						"NDCDEVICEID" to deviceId,
						"NDCAUTH" to "sid=$sessionId"
				)
		)
		this.webSocket = webSocket
		webSocket.run()
	}
}