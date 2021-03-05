package com.churchevents.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class ChatMessage {

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

    public ChatMessage() {
    }

    public ChatMessage(User sender, User recipient, String content, Date timestamp) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = timestamp;
    }
}
