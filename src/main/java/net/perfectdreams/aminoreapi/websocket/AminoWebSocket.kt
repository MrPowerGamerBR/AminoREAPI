package net.perfectdreams.aminoreapi.websocket

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.long
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import net.perfectdreams.aminoreapi.AminoClient
import net.perfectdreams.aminoreapi.events.UnknownEvent
import net.perfectdreams.aminoreapi.events.message.MessageReceivedEvent
import net.perfectdreams.aminoreapi.jsonParser
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

class AminoWebSocket(val client: AminoClient, val url: String, val headers: Map<String, String>) : WebSocketListener() {
    fun run() {
        val client = OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build()

        val request = Request.Builder()
                .url(url)
                .apply {
                    headers.forEach { name, value ->
                        addHeader(name, value)
                    }
                }.build()

        client.newWebSocket(request, this)

        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
        client.dispatcher().executorService().shutdown()
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        println("WebSocket open!")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        println("MESSAGE: $text")

        val json = jsonParser.parse(text)
        val typeId = json["t"].long

        val event = when (typeId) {
            1000L -> {
                MessageReceivedEvent(
                        client,
                        typeId,
                        json["o"]["ndcId"].long,
                        Gson().fromJson(
                                json["o"]["chatMessage"]
                        )
                )
            }
            else -> {
                println("Unsupported event type ID: $typeId")

                UnknownEvent(client, typeId)
            }
        }

        runBlocking {
            client.listeners.onEach {
                it.onEvent(event)
            }
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        System.out.println("MESSAGE: " + bytes.hex())
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(1000, null)
        println("CLOSE: $code $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        println("WebSocket Failure!")
        t.printStackTrace()
    }
}