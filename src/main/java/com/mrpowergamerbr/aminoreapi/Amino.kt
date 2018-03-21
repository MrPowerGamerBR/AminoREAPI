package com.mrpowergamerbr.aminoreapi

import com.github.salomonbrys.kotson.array
import com.github.salomonbrys.kotson.int
import com.github.salomonbrys.kotson.nullString
import com.github.salomonbrys.kotson.registerTypeAdapter
import com.github.salomonbrys.kotson.string
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.mrpowergamerbr.aminoreapi.entities.AminoMedia
import com.mrpowergamerbr.aminoreapi.entities.MediaList
import java.util.*

object Amino {
	var DEBUG = false

	init {
		gson = GsonBuilder().registerTypeAdapter<MediaList> {
			deserialize {
				val mediaList = it.json.array

				val medias = mutableListOf<AminoMedia>()

				for (_media in mediaList) {
					val media = _media.array

					medias.add(AminoMedia(media[0].int, media[1].string, media[2].nullString))
				}

				MediaList(medias)
			}
		}.create()
	}
}

fun _println(obj: Any?) {
	if (Amino.DEBUG)
		println(obj)
}

fun getMessageSignature(): String {
	// generates an random message signature, it seems Amino doesn't care about the signature (yet!)
	return UUID.randomUUID().toString().replace("-", "").toUpperCase().substring(0, 27)
}

val jsonParser = JsonParser()
lateinit var gson: Gson