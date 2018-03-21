package com.mrpowergamerbr.aminoreapi.entities

import java.util.*

class AminoThread(
		val uid: String,
		val membersQuota: Int,
		val membersSummary: List<MiniUserProfile>,
		val threadId: String,
		val keywords: String,
		val membersCount: Int,
		val title: String,
		val membershipStatus: Int,
		val content: String,
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
		val longitude: Long?,
		val extensions: Any?,
		val createdTime: Date?
)