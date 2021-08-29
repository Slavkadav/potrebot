package ru.abe.slaves.potrebot.web.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

private const val CHAT_PEER_PREFIX = 2000000000

data class VkMessage(
    val date: Date,
    @JsonProperty("from_id") val fromId: Int,
    val id: Int,
    val out: Int,
    @JsonProperty("peer_id") val peerId: Int,
    val text: String
) {
    val chatId: Int
        get() = peerId - CHAT_PEER_PREFIX
}