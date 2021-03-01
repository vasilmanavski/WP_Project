package com.churchevents.model;

import com.churchevents.model.enums.Type;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class Post {


    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition="text")
    private String shortDescription;

    @Column(columnDefinition="text")
    private String description; //change to WYSIWYG

    @Lob
    @Column(name = "image")
    private String base64Image;
    // ?

    private Date dateCreated;

    private Integer postClicked;

    private Date dateOfEvent;

    private Integer eventGoing;


    @Enumerated(EnumType.STRING)
    private Type type;

    public Post(){
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Integer getPostClicked() {
        return postClicked;
    }



    public Date getDateOfEvent() {
        return dateOfEvent;
    }

    public void setDateOfEvent(Date dateOfEvent) {
        this.dateOfEvent = dateOfEvent;
    }

    public Integer getEventGoing() {
        return eventGoing;
    }

    public void setEventGoing(Integer eventGoing) {
        this.eventGoing = eventGoing;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

     public Post(Long id,Integer postClicked){
        this.id = id;
        this.postClicked = postClicked;
     }

    public Post(String title, String description,String shortDescription, String base64Image, Type type, Date dateOfEvent) {
        this.title = title;
        this.description = description;
        this.shortDescription = shortDescription;
        this.base64Image = base64Image;
        this.dateCreated = new Date();
        this.type = type;
        this.dateOfEvent = this.type.toString().equals("EVENT")
                ? dateOfEvent
                : null;
       this.eventGoing = 0;
       this.postClicked = 0;
    }
}
