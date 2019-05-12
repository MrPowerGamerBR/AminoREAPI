package net.perfectdreams.aminoreapi.events.message

import net.perfectdreams.aminoreapi.AminoClient
import net.perfectdreams.aminoreapi.entities.AminoMessage
import net.perfectdreams.aminoreapi.events.Event

class MessageReceivedEvent(client: AminoClient, eventId: Long, val ndcId: Long, val message: AminoMessage) : Event(client, eventId) {
    suspend fun retrieveThread() = client.retrieveThreadById(ndcId, message.threadId)
}