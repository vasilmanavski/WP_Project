package com.churchevents.repository;

import com.churchevents.model.Post;
import com.churchevents.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {
    List<Review> findAllByPost(Post post);

    @Override
    void deleteById(Long id);
}
