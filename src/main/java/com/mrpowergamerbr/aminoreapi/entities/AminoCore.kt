package com.mrpowergamerbr.aminoreapi.entities

data class AminoUserProfile(
		val status: Int,
		val itemsCount: Int,
		val consecutiveCheckInDays: String,
		val uid: String,
		val modifiedTime: String,
		val joinedCount: Int,
		val onlineStatus: Int,
		val createdTime: String,
		val longitude: Int,
		val race: String,
		val address: String,
		val membersCount: Int,
		val nickname: String,
		val mediaList: List<Any>,
		val icon: String,
		val mood: String,
		val level: Int,
		val gender: String,
		val age: String,
		val settings: AminoUserSettings,
		val pushEnabled: Boolean,
		val membershipStatus: Int,
		val content: String,
		val reputation: Int,
		val role: Int,
		val latitude: Int,
		val extensions: AminoExtensions,
		val blogsCount: Int)

data class AminoUserSettings(
		val onlineStatus: Int
)