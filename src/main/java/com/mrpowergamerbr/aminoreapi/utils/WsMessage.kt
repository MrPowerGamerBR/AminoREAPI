package com.mrpowergamerbr.aminoreapi.utils

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

class WsMessage(@SerializedName("t") val type: Int, @SerializedName("o") val jsonObject: JsonObject)