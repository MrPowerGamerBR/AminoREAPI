package net.perfectdreams.aminoreapi.utils

object MiscUtils {
    fun toLongCommunityId(id: String): Long {
        return if (!id[0].isDigit()) // If the ID is "x123456789"
            id.substring(1).toLong()
        else
            id.toLong()
    }
}