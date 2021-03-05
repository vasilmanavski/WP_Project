package com.churchevents.model;

import lombok.Data;

import java.util.Date;

@Data
public class ChatMessagePayload {

    private String senderId;

    private String recipientId;

    private String content;

    private Date timestamp;

    public ChatMessagePayload() {
    }

    public ChatMessagePayload(String senderId, String recipientId, String content, Date timestamp) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.content = content;
        this.timestamp = timestamp;
    }
}
