package com.mrpowergamerbr.aminoreapi

import com.github.kevinsawicki.http.HttpRequest
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject
import com.mrpowergamerbr.aminoreapi.entities.AminoMessage
import com.mrpowergamerbr.aminoreapi.entities.AminoReminders
import com.mrpowergamerbr.aminoreapi.entities.AminoThread
import com.mrpowergamerbr.aminoreapi.entities.CheckInResponse
import com.mrpowergamerbr.aminoreapi.entities.CommunityInfo
import com.mrpowergamerbr.aminoreapi.entities.DeviceInfo
import com.mrpowergamerbr.aminoreapi.entities.JoinedCommunitiesInfo
import com.mrpowergamerbr.aminoreapi.entities.PublicChats
import com.mrpowergamerbr.aminoreapi.utils.Endpoints
import com.mrpowergamerbr.aminoreapi.utils.IncorrentLoginException
import com.mrpowergamerbr.aminoreapi.utils.InvalidPasswordException
import java.io.File
import java.net.URLEncoder
import java.nio.file.Files
import java.nio.file.Paths
import javax.xml.bind.DatatypeConverter
import javassist.CtMethod.ConstParameter.string
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException



class AminoClient(val email: String, val password: String, val deviceId: String) {
	var sessionId: String? = null

	fun login() {
		// required for ws3.narvii.com
		System.setProperty("https.protocols", "TLSv1.1")

		val payload = JsonObject()
		payload["email"] = email
		payload["secret"] = "0 $password" // TODO: Always 0?
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

	fun getJoinedCommunities(start: Int, size: Int): JoinedCommunitiesInfo {
		val body = HttpRequest.get(Endpoints.JOINED_COMMUNITIES.format(start, size))
				.header("NDCDEVICEID", deviceId)
				.header("NDCAUTH", "sid=$sessionId")
				.body()

		_println(body)

		return gson.fromJson(body)
	}

	fun getCommunityInfo(ndcId: String): CommunityInfo {
		val body = HttpRequest.get(Endpoints.COMMUNITY_INFO.format(ndcId))
				.header("NDCDEVICEID", deviceId)
				.header("NDCAUTH", "sid=$sessionId")
				.body()

		_println(body)
		return gson.fromJson(body)
	}

	fun getThread(ndcId: String, threadId: String): AminoThread {
		val body = HttpRequest.get(Endpoints.COMMUNITY_THREAD.format(ndcId, threadId))
				.header("NDCDEVICEID", deviceId)
				.header("NDCAUTH", "sid=$sessionId")
				.body()

		_println(body)
		return gson.fromJson(body)
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
		payload["mediaUploadValue"] = DatatypeConverter.printBase64Binary(file.readBytes())
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

	fun websocketUpgrade(url: String) {
		val client = OkHttpClient()
		val request = Request.Builder()
				.url(url)
				.build()

		client.newCall(request).execute().use({ response ->
			System.out.println(response.body())
		})
	}

	fun get(url: String, headers: Map<String, String> = mutableMapOf("NDCDEVICEID" to deviceId, "NDCAUTH" to "sid=$sessionId"), vararg variables: Any): String {
		val body = HttpRequest.get(url)
				.headers(headers)
				.header("NDCDEVICEID", deviceId)
				.header("NDCAUTH", "sid=$sessionId")
				.header("NDC-MSG-SIG", getMessageSignature())
				.header("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 7.1.2; MotoG3-TE Build/NJH47F; com.narvii.amino.master/1.8.15305)")
				.header("Upgrade", "websocket")
				.header("Connection", "Upgrade")
				.header("Sec-WebSocket-Key", "86iFBnuI8GWLlgWmSToY6g==")
				.header("Sec-WebSocket-Version", "13")
				.header("Accept-Encoding", "gzip")
				.body()

		_println(body)

		return body
	}

	fun post(url: String, headers: Map<String, String> = mutableMapOf("NDCDEVICEID" to deviceId, "NDCAUTH" to "sid=$sessionId"), vararg variables: Any): String {
		val body = HttpRequest.post(url.format(variables))
				.headers(headers)
				.body()

		_println(body)

		return body
	}
}