package com.churchevents.model;

import com.churchevents.model.enums.Rating;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Rating rating;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    private Post post;

    public Review() {

    }

    public Review(Rating rating, User user, Post post) {
        this.rating = rating;
        this.user = user;
        this.post = post;
    }
}
