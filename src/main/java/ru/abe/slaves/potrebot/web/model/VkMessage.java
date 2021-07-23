package ru.abe.slaves.potrebot.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class VkMessage {
    public static final int CHAT_PEER_PREFIX = 2000000000;

    private Date date;
    @JsonProperty("from_id")
    private int fromId;
    private int id;
    private int out;
    @JsonProperty("peer_id")
    private int peerId;
    private String text;

    public int getChatId() {
        return peerId - CHAT_PEER_PREFIX;
    }
}
