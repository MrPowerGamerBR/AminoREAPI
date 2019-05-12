package net.perfectdreams.aminoreapi.hooks

import net.perfectdreams.aminoreapi.events.Event
import net.perfectdreams.aminoreapi.events.message.MessageReceivedEvent

open class ListenerAdapter : EventListener {
    open suspend fun onMessageReceived(event: MessageReceivedEvent) {}

    override suspend fun onEvent(event: Event) {
        when (event) {
            is MessageReceivedEvent -> onMessageReceived(event)
        }
    }
}