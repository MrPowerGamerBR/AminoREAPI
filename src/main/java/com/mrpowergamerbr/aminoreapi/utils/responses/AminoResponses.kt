package com.mrpowergamerbr.aminoreapi.utils.responses

import com.mrpowergamerbr.aminoreapi.entities.AminoHomeCommunity
import com.mrpowergamerbr.aminoreapi.entities.AminoUserProfile

data class AminoJoinedCommunitiesResponse(
		val _jsonResponse: String,
		val communityList: List<AminoHomeCommunity>,
		val userInfoInCommunities: Map<String, AminoUserProfile>) : AminoResponse(_jsonResponse)

data class AminoAffiliationsResponse(val _jsonResponse: String, val affiliations: List<String>) : AminoResponse(_jsonResponse)

