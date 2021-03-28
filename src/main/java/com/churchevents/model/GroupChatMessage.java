package com.churchevents.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class GroupChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private Date timestamp;

    @ManyToOne
    private User sender;

    @ManyToOne
    private GroupChat groupChat;

    public GroupChatMessage() {
    }

    public GroupChatMessage(String content, Date timestamp, User sender, GroupChat groupChat) {
        this.content = content;
        this.timestamp = timestamp;
        this.sender = sender;
        this.groupChat = groupChat;
    }
}
