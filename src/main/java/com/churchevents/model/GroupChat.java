package com.churchevents.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
public class GroupChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Date dateCreated;

    public GroupChat() {
    }

    public GroupChat(String name) {
        this.name = name;
        this.dateCreated = new Date();
    }
}
