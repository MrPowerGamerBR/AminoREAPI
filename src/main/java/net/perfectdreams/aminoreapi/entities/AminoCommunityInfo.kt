package net.perfectdreams.aminoreapi.entities

import java.util.*

class AminoCommunityInfo(
		val status: Int,
		val launchPage: LaunchPage,
		val endpoint: String,
		val name: String,
		val modifiedTime: Any,
		val communityHeat: Double,
		val tagline: String,
		val templateId: Int,
		val agent: Any,
		val joinType: Int,
		val link: String,
		val listedStatus: Int,
		val themePack: ThemePack,
		val ndcId: Int,
		val createdTime: Any,
		val probationStatus: Int,
		val membersCount: Int,
		val primaryLanguage: String,
		val promotionalMediaList: MediaList?,
		val icon: String,

		// INFO EXCLUSIVE
		val keywords: String,
		val searchable: Boolean,
		val content: String?,
		val advancedSettings: Any,
		val communityHeadList: Any?,
		val configuration: Any?
)