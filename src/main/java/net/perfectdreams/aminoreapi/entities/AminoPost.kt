package net.perfectdreams.aminoreapi.entities

class AminoPost(
		val author: MiniUserProfile,
		val blogId: String,
		val commentsCount: Int,
		val content: String?,
		val contentRating: Int,
		val createdTime: String,
		val endTime: String?,
		val extensions: Any?,
		val guestVotesCount: Int,
		val keywords: String?,
		val latitude: String?,
		val longitude: String?,
		val mediaList: MediaList?,
		val modifiedTime: String?,
		val status: Int,
		val style: Int,
		val title: String,
		val type: Int,
		val votedValue: Int,
		val votesCount: Int
)