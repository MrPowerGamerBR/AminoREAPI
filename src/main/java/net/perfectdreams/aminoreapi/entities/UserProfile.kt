package net.perfectdreams.aminoreapi.entities

import java.util.*

class UserProfile(
		val status: Int,
		val itemsCount: Int,
		val consecutiveCheckInDays: String?,
		val uid: String,
		val modifiedTime: Any,
		val joinedCount: Any,
		val longitude: Long,
		val race: String?,
		val address: String,
		val membersCount: Int,
		val nickname: String,
		val mediaList: Any,
		val icon: String,
		val mood: String?,
		val level: Int,
		val gender: String?,
		val settings: Any,
		val pushEnabled: Boolean,
		val membershipStatus: Int,
		val content: String?,
		val reputation: Int,
		val role: Int,
		val latitude: Long,
		val extensions: Any?,
		val blogsCount: Int
)