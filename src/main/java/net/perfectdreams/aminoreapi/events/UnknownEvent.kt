package net.perfectdreams.aminoreapi.events

import net.perfectdreams.aminoreapi.AminoClient

class UnknownEvent(client: AminoClient, eventId: Long) : Event(client, eventId)