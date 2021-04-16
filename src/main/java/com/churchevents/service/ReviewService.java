package com.churchevents.service;

import com.churchevents.model.Post;
import com.churchevents.model.Review;
import com.churchevents.model.User;
import com.churchevents.model.enums.Rating;

public interface ReviewService {
    Review create(Rating rating, User user, Post post);

}
