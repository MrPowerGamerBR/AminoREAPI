package net.perfectdreams.aminoreapi.entities

class AminoReminders(
		val noticesCount: Int,
		val notificationsCount: Int,
		val consecutiveCheckInDays: Int,
		val unreadChatThreadsCount: Int,
		val hasCheckInToday: Boolean
)