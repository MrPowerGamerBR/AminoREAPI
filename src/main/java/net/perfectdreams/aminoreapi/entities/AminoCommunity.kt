package net.perfectdreams.aminoreapi.entities

import java.util.*

class AminoCommunity(
		val status: Int,
		val launchPage: LaunchPage,
		val endpoint: String,
		val name: String,
		val modifiedTime: Date,
		val communityHeat: Double,
		val tagline: String,
		val templateId: Int,
		val agent: Any,
		val joinType: Int,
		val link: String,
		val listedStatus: Int,
		val themePack: ThemePack,
		val ndcId: Int,
		val createdTime: Date,
		val probationStatus: Int,
		val membersCount: Int,
		val primaryLanguage: String,
		val promotionalMediaList: MediaList?,
		val icon: String
)