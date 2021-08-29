package ru.abe.slaves.potrebot.web.model

import com.fasterxml.jackson.annotation.JsonProperty

data class VkEvent(
    val type: String?,
    @JsonProperty("object") val content: VkEventContent,
    @JsonProperty("group_id") val groupId: Long = 0,
    @JsonProperty("event_id") val eventId: String? = null
)