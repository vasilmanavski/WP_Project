package com.churchevents.model;

import com.churchevents.model.enums.MessagePurpose;
import com.churchevents.model.enums.MessageStatus;
import com.churchevents.model.enums.MessageType;
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

    private MessageType messageType;

    private MessageStatus messageStatus;

    private MessagePurpose messagePurpose;

    public ChatMessagePayload() {
    }

    public ChatMessagePayload(Long id, String senderId, String recipientId, String content, Date timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public ChatMessagePayload(Long id, String senderId, String recipientId, String content, Date timestamp,
                              MessageStatus messageStatus) {
        this.id = id;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.content = content;
        this.timestamp = timestamp;
        this.messageStatus = messageStatus;
    }
}
