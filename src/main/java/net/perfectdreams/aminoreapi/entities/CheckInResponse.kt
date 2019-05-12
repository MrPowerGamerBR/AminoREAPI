package net.perfectdreams.aminoreapi.entities

class CheckInResponse(
		val consecutiveCheckInDays: Int,
		val earnedReputationPoint: Int,
		val additionalReputationPoint: Int,
		val userProfile: MiniUserProfile
)