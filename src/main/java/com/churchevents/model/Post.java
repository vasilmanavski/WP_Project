package com.churchevents.model;

import lombok.Data;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Data
@Entity
public class Post{


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


    private Integer eventGoing;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    public Post(){
    }

    public void setPostClicked(Integer postClicked) {
        this.postClicked = postClicked;
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



    public Integer getDayOfEvent(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.getDateCreated());

        return calendar.get(calendar.DAY_OF_MONTH);
    }

    public String getMonthOfEvent(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.getDateCreated());

        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        return month_date.format(calendar.getTime());
    }

    public Integer getEventGoing() {
        return eventGoing;
    }

    public void setEventGoing(Integer eventGoing) {
        this.eventGoing = eventGoing;
    }


    public Post(String title, String description,String shortDescription, String base64Image, User user) {
        this.title = title;
        this.description = description;
        this.shortDescription = shortDescription;
        this.user = user;
        this.base64Image = base64Image;
        this.dateCreated = new Date();
       this.eventGoing = 0;
       this.postClicked = 0;
    }

}