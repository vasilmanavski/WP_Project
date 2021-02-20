package com.churchevents.model;

import com.churchevents.model.enums.Type;
import lombok.Data;

import javax.persistence.*;
import java.sql.Blob;
import java.util.Date;

@Data
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description; //change to WYSIWYG

    @Lob
    private Blob image; // ?

    private Date dateCreated;

    private Integer postClicked;

    private Date dateOfEvent;

    private Integer eventGoing;


    @Enumerated(EnumType.STRING)
    private Type type;

    public Post(){
    }

    public Post(String title, String description, Blob image, Type type, Date dateOfEvent) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.dateCreated = new Date();
        this.type = type;
        this.dateOfEvent = this.type.toString().equals("EVENT")
                ? dateOfEvent
                : null;
        this.eventGoing = 0;
        this.postClicked = 0;
    }
}
