package com.mrpowergamerbr.aminoreapi.entities

import com.github.kevinsawicki.http.HttpRequest
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import com.mrpowergamerbr.aminoreapi.Amino
import com.mrpowergamerbr.aminoreapi.AminoClient
import com.mrpowergamerbr.aminoreapi.utils.Endpoints

data class AminoCommunity(
		var aminoClient: AminoClient,
		var id: String,
		var searchable: Boolean = false,
		@SerializedName("agent")
		var leader: AminoUser, // Seems to be leader
		var listedStatus: Int,
		var probationStatus: Int,
		var keywords: String,
		var themePack: AminoThemePack,
		val membersCount: Int,
		val primaryLanguage: String,
		val communityHeat: String,
		val mediaList: String,
		val content: String,
		val tagline: String,
		val advancedSettings: AminoCommunityAdvancedSettings,
		val joinType: Int,
		val status: Int,
		val modifiedTime: String,
		val ndcId: Int,
		val link: String,
		val icon: String,
		val endpoint: String,
		val name: String,
		val extensions: String,
		val templateId: Int,
		val createdTime: String) {

	fun join() {
		var response = HttpRequest
				.post(String.format(Endpoints.JOIN_COMMUNITY, id))
				.header("NDCAUTH", "sid=" + aminoClient.sid)
				.acceptJson()
				.body();

		println(response)
	}

	fun join(invitationUrl: String) {
		// First we are going to get the invitation ID
		var invitationId = aminoClient.getInvitationInfo(invitationUrl).invitationId

		val innerObject = JsonObject()

		innerObject.addProperty("invitationId", invitationId);

		// Then we are going to send a join community request with the invitationId
		var response = HttpRequest
				.post(String.format(Endpoints.JOIN_COMMUNITY, id))
				.header("NDCAUTH", "sid=" + aminoClient.sid)
				.acceptJson()
				.send(innerObject.toString())
				.body();

		println(response)
	}

	fun leave() {
		var response = HttpRequest
				.post(String.format(Endpoints.LEAVE_COMMUNITY, id))
				.header("NDCAUTH", "sid=" + aminoClient.sid)
				.acceptJson()
				.body();

		println(response)
	}

	fun getBlogFeed(start: Int, size: Int): List<AminoBlogPost> {
		var response = HttpRequest
				.get(String.format(Endpoints.COMMUNITY_FEED, id, start, size))
				.header("NDCAUTH", "sid=" + aminoClient.sid)
				.acceptJson()
				.body();

		println(response);

		var parser = JsonParser();
		var parsedJson = parser.parse(response).asJsonObject.get("blogList").asJsonArray;

		var blogFeed = Amino.gson.fromJson<List<AminoBlogPost>>(parsedJson)

		return blogFeed;
	}
}

data class AminoUser(
		val status: Int,
		val uid: String,
		val level: String,
		val mood: String,
		val reputation: Int,
		val role: Int,
		val nickname: String,
		val icon: String)

data class AminoThemePack(
		val themeColor: String,
		val themePackHash: String,
		val themePackRevision: Int,
		val themePackUrl: String)

data class AminoCommunityAdvancedSettings(
		val defaultRankingTypeInLeaderboard: Int,
		val frontPageLayout: Int,
		val hasPendingReviewRequest: Boolean,
		val welcomeMessageEnabled: String,
		val welcomeMessageText: String,
		val pollMinFullBarVoteCount: Int,
		val catalogEnabled: Boolean)

data class AminoInvitation(
		val status: Int,
		val duration: Int,
		val invitationId: String,
		val link: String,
		val modifiedTime: String,
		val ndcId: Int,
		val createdTime: String,
		val inviteCode: String)

data class AminoBlogPost(
		val status: Int,
		val style: Int,
		val modifiedTime: String,
		val title: String,
		val author: AminoUser,
		val contentRating: Int,
		val votedValue: Int,
		val content: String,
		val keywords: String,
		val latitude: Int,
		val longitude: Int,
		val endTime: String,
		val type: Int,
		val blogId: String,
		val commentsCount: Int,
		val mediaList: List<Object>)