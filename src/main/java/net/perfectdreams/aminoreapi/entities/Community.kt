package net.perfectdreams.aminoreapi.entities

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.get
import net.perfectdreams.aminoreapi.AminoClient
import net.perfectdreams.aminoreapi.gson
import net.perfectdreams.aminoreapi.jsonParser
import net.perfectdreams.aminoreapi.utils.Endpoints

class Community(
        val status: Int,
        val endpoint: String,
        val name: String,
        val ndcId: Long,
        var userInfo: UserInfo? = null
) {
    lateinit var client: AminoClient

    suspend fun getThreads(type: String, start: Int = 0, size: Int = 25) : List<Thread> {
        val payload = client.get(Endpoints.COMMUNITY_CHAT_THREAD.format(this.ndcId, type, start, size))
        val json = jsonParser.parse(payload)

        return gson.fromJson<List<Thread>>(json["threadList"]).onEach { it.client = client; it.community = this }
    }
}