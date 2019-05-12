package net.perfectdreams.aminoreapi.events

import net.perfectdreams.aminoreapi.AminoClient

abstract class Event(val client: AminoClient, val eventId: Long) {

}