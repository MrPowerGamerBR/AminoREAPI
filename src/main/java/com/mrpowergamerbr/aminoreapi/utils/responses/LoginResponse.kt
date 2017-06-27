package com.mrpowergamerbr.aminoreapi.utils.responses

class LoginResponse(jsonResponse: String) : AminoResponse(jsonResponse) {
	lateinit var secret: String;
	lateinit var sid: String;
	lateinit var account: AminoAccountStatus;
}

data class AminoAccountStatus(
		val uid: String)