package net.perfectdreams.aminoreapi.entities

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.annotations.SerializedName
import net.perfectdreams.aminoreapi.AminoClient
import net.perfectdreams.aminoreapi._println
import net.perfectdreams.aminoreapi.gson
import net.perfectdreams.aminoreapi.jsonParser
import net.perfectdreams.aminoreapi.utils.Endpoints
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO
import javax.xml.bind.DatatypeConverter

class Thread(
		@SerializedName("uid")
		val uniqueId: UUID,
		val membersQuota: Int,
		val membersSummary: List<MiniUserProfile>,
		val threadId: UUID,
		val keywords: String?,
		val membersCount: Int,
		val title: String,
		val membershipStatus: Int,
		val content: String,
		val needHidden: Boolean,
		val latitude: Long?,
		val alertOption: Int,
		val lastReadTime: Date?,
		val type: Int,
		val status: Int,
		val modifiedTime: Date?,
		val lastMessageSummary: Any?,
		val condition: Int,
		val icon: String,
		val latestActivityTime: Date?,
		// TODO: Author
		val longitude: Long?,
		val extensions: Any?,
		val createdTime: Date?
) {
	lateinit var community: Community
	lateinit var client: AminoClient


	fun getThreadMessages(start: Int, size: Int, startTime: String? = null): List<AminoMessage> {
		val url = if (startTime != null) {
			Endpoints.COMMUNITY_CHAT_GET_MESSAGES_SINCE.format(community.ndcId, threadId, start, size, startTime)
		} else {
			Endpoints.COMMUNITY_CHAT_GET_MESSAGES.format(community.ndcId, threadId, start, size)
		}

		val body = client.get(url)

		_println(body)
		return gson.fromJson(jsonParser.parse(body)["messageList"])
	}

	fun sendMessage(content: String) {
		client.post(
				Endpoints.COMMUNITY_CHAT_SEND_MESSAGE.format(community.ndcId, threadId), payload = jsonObject(
				"content" to content,
				"type" to 0, // there is also clientRefId and timestamp sent by the vanilla client, but it seems Amino doesn't care about them
				// technically, if you don't send a clientRefId, it will be magically set to 0 by the server
				// the timestamp isn't used at all
                // to avoid sync issues, let's use the current time millis
				"clientRefId" to System.currentTimeMillis() / 1000,
				"timestamp" to System.currentTimeMillis() / 1000
		)
		)
	}

	fun sendImage(image: BufferedImage) {
		val outputStream = ByteArrayOutputStream()
		outputStream.use {
			ImageIO.write(image, "png", it)
		}

		val inputStream = ByteArrayInputStream(outputStream.toByteArray())

		client.post(
				Endpoints.COMMUNITY_CHAT_SEND_MESSAGE.format(community.ndcId, threadId), payload = jsonObject(
				"content" to null,
				"type" to 0,
				"mediaType" to 100,
				"mediaUploadValue" to DatatypeConverter.printBase64Binary(inputStream.readBytes()),
				"mediaUhqEnabled" to false,
				"mediaUploadValueContentType" to "image/png",
				"attachedObject" to null,
				"clientRefId" to System.currentTimeMillis() / 1000,
				"timestamp" to System.currentTimeMillis() / 1000
		)
		)
	}
}