package net.perfectdreams.aminoreapi.entities

import com.google.gson.annotations.SerializedName
import java.util.*

class AminoMessage(
		@SerializedName("uid")
		val uniqueId: UUID,
		val author: MiniUserProfile,
		val threadId: String,
		val mediaType: Int,
		val content: String,
		val mediaValue: String?,
		val clientRefId: Long,
		val messageId: UUID,
		val createdTime: Date,
		val type: Int
)