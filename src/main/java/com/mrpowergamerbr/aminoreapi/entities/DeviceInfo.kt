package com.mrpowergamerbr.aminoreapi.entities

import com.google.gson.annotations.SerializedName

class DeviceInfo(
		@SerializedName("deviceID")
		val deviceId: String,
		@SerializedName("bundleID")
		val bundleId: String,
		val clientType: Int,
		val timezone: Int,
		val systemPushEnabled: Boolean,
		val deviceToken: String,
		val deviceTokenType: Int,
		val timestamp: Long
)