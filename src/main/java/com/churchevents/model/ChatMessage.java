package com.churchevents.model;

import com.churchevents.model.enums.MessageStatus;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class ChatMessage {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
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

    public MessageStatus getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fk_sender")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "fk_recipient")
    private User recipient;

    private String content;

    private Date timestamp;

    @Enumerated(value = EnumType.STRING)
    private MessageStatus messageStatus;

    public ChatMessage() {
    }

    public ChatMessage(User sender, User recipient, String content, Date timestamp) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = timestamp;
        this.messageStatus = MessageStatus.DELIVERED;
    }
}
