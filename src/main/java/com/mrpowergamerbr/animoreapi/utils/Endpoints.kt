package com.mrpowergamerbr.animoreapi.utils

object Endpoints {
	val PREFIX = "http://service.narvii.com/api/v1";

	// ===[ AUTH ENDPOINTS ]===
	val LOGIN = PREFIX + "/g/s/auth/login";
	val REGISTER = PREFIX + "/g/s/auth/register"
	val REGISTER_CHECK = PREFIX + "/g/s/auth/register-check"

	// ===[ USER ENDPOINTS ]===
	val AFFILIATIONS = PREFIX + "/g/s/account/affiliations?type=active";
	val HEADLINES = PREFIX + "/g/s/feed/headlines?start=%s&size=%s"
	val DEVICE_INFO = PREFIX + "/device"

	// ===[ COMMUNITY ENDPOINTS ]===
	val LINK_IDENTIFY = PREFIX + "/g/s/community/link-identify?q=%s";
	val JOIN_COMMUNITY = PREFIX + "/%s/s/community/join";
	val LEAVE_COMMUNITY = PREFIX + "/%s/s/community/leave";
	val COMMUNITY_INFO = PREFIX + "/g/s-%s/community/info"
	val SUGGESTED_COMMUNITIES = PREFIX + "/g/s/community/suggested?language=%s";
	val TRENDING_COMMUNITIES = PREFIX + "/g/s/community/trending?start=%s&size=%s&language=%s";
	val COMMUNITY_FEED = PREFIX + "/%s/s/feed/blog-all?start=%s&size=%s"
	val COMMUNITY_ONLINE_MEMBERS = PREFIX + "/%s/s/community/online-members-check"
	val SUGGESTED_KEYWORDS = PREFIX + "/g/s/community/suggested-keywords?q=%s&start=%s&size=%s&language=%s";

	// ===[ NOTIFICATION ENDPOINTS ]===
	val COMMUNITY_NOTIFICATIONS = PREFIX + "/%s/s/notification?start=%s&size=%s&cv=%s"

	// ===[ CHAT ENDPOINTS ]===
	val COMMUNITY_CHAT_SEND_MESSAGE = PREFIX + "/%s/s/chat/thread/%s/message"
	val COMMUNITY_CHAT_GET_MESSAGES = PREFIX + "/%s/s/chat/thread/%s/message?start=%s&size=%s&cv=1.2"
}