package com.churchevents.model;

import com.churchevents.model.enums.MessagePurpose;
import com.churchevents.model.enums.MessageStatus;
import com.churchevents.model.enums.MessageType;
import lombok.Data;

import java.util.Date;

@Data
public class ChatMessagePayload {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

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
