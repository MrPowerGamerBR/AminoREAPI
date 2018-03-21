package com.mrpowergamerbr.aminoreapi.entities

import java.util.*

class AminoMessage(
		val author: MiniUserProfile,
		val threadId: String,
		val mediaType: Int,
		val content: String,
		val mediaValue: String?,
		val clientRefId: String,
		val messageId: String,
		val createdTime: Date,
		val type: Int
)