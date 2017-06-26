package com.mrpowergamerbr.aminoreapi.utils.responses

import com.google.gson.annotations.SerializedName

open class AminoResponse {
	lateinit var jsonResponse: String;
	@SerializedName("api:message")
	lateinit var apiMessage: String;
	@SerializedName("api:timestamp")
	lateinit var apiTimestamp: String;
	@SerializedName("api:duration")
	lateinit var apiDuration: String;
	@SerializedName("api:statuscode")
	var apiStatusCode: Int = 0;

	constructor(jsonResponse: String) {
		this.jsonResponse = jsonResponse;
	}
}