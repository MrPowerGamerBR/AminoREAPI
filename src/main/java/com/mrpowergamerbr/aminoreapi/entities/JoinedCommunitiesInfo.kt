package com.mrpowergamerbr.aminoreapi.entities

class JoinedCommunitiesInfo(
		val communityList: List<AminoCommunity>,
		val userInfoInCommunities: Map<String, UserProfile>
)