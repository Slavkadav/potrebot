package ru.abe.slaves.potrebot.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VkEvent {
    private String type;
    private VkEventContent object;
    @JsonProperty("group_id")
    private long groupId;
    @JsonProperty("event_id")
    private String eventId;
}
