package com.mrpowergamerbr.aminoreapi.utils.responses

class LoginResponse(jsonResponse: String) : AminoResponse(jsonResponse) {
	lateinit var secret: String;
	lateinit var sid: String;
}