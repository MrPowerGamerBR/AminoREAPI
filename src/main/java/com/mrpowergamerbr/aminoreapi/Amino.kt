package com.mrpowergamerbr.aminoreapi

import com.github.kevinsawicki.http.HttpRequest
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mrpowergamerbr.aminoreapi.utils.Endpoints

object Amino {
	val gson: Gson = Gson();

	fun registerCheck(email: String, deviceId: String) {
		val innerObject = JsonObject()

		innerObject.addProperty("email", email) // TODO: How the phone login is handled?
		innerObject.addProperty("deviceID", deviceId) // TODO: Auto generated device ID?

		var response = HttpRequest
				.post(Endpoints.REGISTER_CHECK)
				.acceptJson()
				.send(innerObject.toString())
				.body();

		println(response);
	}

	/**
	 *
	 */
	fun createAccount(email: String, password: String, deviceId: String, nickname: String) {
		registerCheck(email, deviceId);
		val innerObject = JsonObject()

		innerObject.addProperty("email", email) // TODO: How the phone login is handled?
		innerObject.addProperty("secret", "0 " + password) // TODO: Is the secret always prefixed by "0 "?
		innerObject.addProperty("deviceID", deviceId) // TODO: Auto generated device ID?
		innerObject.addProperty("deviceID2", deviceId) // TODO: Why so many different device IDs?
		innerObject.addProperty("deviceID3", deviceId)
		innerObject.addProperty("deviceID4", deviceId)
		innerObject.addProperty("deviceID5", deviceId)
		innerObject.addProperty("clientType", 100) // TODO: Other client types?
		innerObject.addProperty("action", "normal") // TODO: What are the other actions? (register?)
		innerObject.addProperty("val1", 16) // TODO: What is this?
		innerObject.addProperty("val2", 81920)
		innerObject.addProperty("clientCallbackURL", "narviiapp://relogin")
		innerObject.addProperty("address", "São Paulo, Brasil")
		innerObject.addProperty("nickname", "São Paulo, Brasil")
		innerObject.addProperty("latitude", -0)
		innerObject.addProperty("longitude", -0)

		var response = HttpRequest
				.post(Endpoints.REGISTER)
				.acceptJson()
				.send(innerObject.toString())
				.body();

		println(response);
	}
}