package com.churchevents.service.impl;

import com.churchevents.model.Post;
import com.churchevents.model.Review;
import com.churchevents.model.User;
import com.churchevents.model.enums.Rating;
import com.churchevents.repository.PostRepository;
import com.churchevents.repository.ReviewRepository;
import com.churchevents.repository.UserRepository;
import com.churchevents.service.ReviewService;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final PostRepository postRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewServiceImpl(PostRepository postRepository, ReviewRepository reviewRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Review create(Rating rating, User user, Post post) {
        Review review = new Review(rating,user,post);
        return  this.reviewRepository.save(review);
    }
}
