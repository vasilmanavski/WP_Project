package com.churchevents.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class GroupChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Date dateCreated;

    @JsonIgnore
    @OneToMany(mappedBy = "groupChat")
    private List<GroupChatMessage> groupChatMessages;

    public GroupChat() {
    }

    public GroupChat(String name) {
        this.name = name;
        this.dateCreated = new Date();
    }
}
