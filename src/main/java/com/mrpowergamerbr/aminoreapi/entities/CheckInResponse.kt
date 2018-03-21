package com.mrpowergamerbr.aminoreapi.entities

class CheckInResponse(
		val consecutiveCheckInDays: Int,
		val earnedReputationPoint: Int,
		val additionalReputationPoint: Int,
		val userProfile: MiniUserProfile
)