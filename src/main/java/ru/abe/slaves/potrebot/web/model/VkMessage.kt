package ru.abe.slaves.potrebot.web.model

import com.google.gson.annotations.SerializedName

private const val CHAT_PEER_PREFIX = 2000000000

data class VkMessage(
    @SerializedName("from_id")
    val fromId: Int,
    val id: Int,
    val out: Int,
    @SerializedName("peer_id")
    val peerId: Int,
    val text: String
) {
    val chatId: Int
        get() = peerId - CHAT_PEER_PREFIX
}