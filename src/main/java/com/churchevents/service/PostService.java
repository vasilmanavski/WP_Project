package com.churchevents.service;

import com.churchevents.model.Post;
import com.churchevents.model.enums.Type;
import javassist.bytecode.ByteArray;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Blob;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PostService {
    List<Post> listAllPosts();

    Page<Post> findAllWithPagination(Pageable pageable);
    Post findById(Long id);

    Post findByTitle(String title);

    List<Post> lastPosts();

    Post create(String title, String description,String shortDescription, MultipartFile image, Type type, Date dateOfEvent) throws IOException;

    Post update(Long id, String title, String description,String shortDescription, MultipartFile image, Type type, Date dateOfEvent) throws IOException;
    Post click(Long id);
    Post delete(Long id);
    void sortPosts(List<Post> posts);
    void deleteByPost(Post post);

}

