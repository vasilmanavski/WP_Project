package com.churchevents.service;

import com.churchevents.model.Post;
import com.churchevents.model.enums.Type;
import javassist.bytecode.ByteArray;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Blob;
import java.util.Date;
import java.util.List;

public interface PostService {
    List<Post> listAllPosts();

    Post findById(Long id);

    Post create(String title, String description, MultipartFile image, Type type, Date dateOfEvent) throws IOException;

    Post update(Long id, String title, String description, MultipartFile image, Type type, Date dateOfEvent) throws IOException;

    Post delete(Long id);



}
