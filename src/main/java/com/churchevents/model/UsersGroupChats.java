package com.churchevents.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class UsersGroupChats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private GroupChat groupChat;

    private Date dateWhenUserLeftGroupChat; // can rejoin

    public UsersGroupChats() {
    }

    public UsersGroupChats(User user, GroupChat groupChat) {
        this.user = user;
        this.groupChat = groupChat;
        this.dateWhenUserLeftGroupChat = null;
    }
}
