package net.perfectdreams.aminoreapi.hooks

import net.perfectdreams.aminoreapi.events.Event

interface EventListener {
    suspend fun onEvent(event: Event)
}