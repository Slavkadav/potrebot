package ru.abe.slaves.potrebot.web.model

import com.google.gson.annotations.SerializedName

data class VkEvent(
    val type: String?,
    @SerializedName("object") val content: VkEventContent,
    @SerializedName("group_id") val groupId: Long = 0,
    @SerializedName("event_id") val eventId: String
)