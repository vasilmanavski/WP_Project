package com.churchevents.model;

import lombok.Data;

import java.util.Date;

@Data
public class ChatMessagePayload {

    private Long id;

    private String senderId;

    private String recipientId;

    private String content;

    private Date timestamp;

    private Boolean typing;

    public ChatMessagePayload() {
    }

    public ChatMessagePayload(Long id, String senderId, String recipientId, String content, Date timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.content = content;
        this.timestamp = timestamp;
    }
}
