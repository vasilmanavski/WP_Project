package com.churchevents.service;

import com.churchevents.model.Post;
import com.churchevents.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface PostService {
    List<Post> listAllPosts();

    Post findById(Long id);

    Post findByTitle(String title);

    List<Post> lastPosts();

    Post create(String title, String description,String shortDescription, MultipartFile image, User user) throws IOException;

    Post update(Long id, String title, String description, String shortDescription, MultipartFile image, User user) throws IOException;
    Post click(Long id);
    Post delete(Long id);
    void sortPosts(List<Post> posts);
    void deleteByPost(Post post);
    Page<Post> findPaginated(Pageable pageable);

}

