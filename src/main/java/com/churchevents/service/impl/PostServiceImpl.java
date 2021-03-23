package com.churchevents.service.impl;

import com.churchevents.model.Post;
import com.churchevents.model.enums.Type;
import com.churchevents.model.exceptions.InvalidPostIdException;
import com.churchevents.repository.PostRepository;
import com.churchevents.service.PostService;

import javassist.bytecode.ByteArray;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Blob;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.Base64;
import java.util.Date;
import java.util.List;
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<Post> listAllPosts() {
        return this.postRepository.findAll();
    }

    @Override
    public Post findById(Long id) {

       return this.postRepository.findById(id).orElseThrow(InvalidPostIdException::new);
    }



    @Override
    public Post create(String title, String description,String shortDescription, MultipartFile image, Type type, Date dateOfEvent) throws IOException {

        Post post = new Post();
        if (image != null) {
            byte[] imageBytes = image.getBytes();
            String base64Image = String.format("data:%s;base64,%s", image.getContentType(), Base64.getEncoder().encodeToString(imageBytes));
            post.setBase64Image(base64Image);
        }


        post = new Post(title,description,shortDescription, post.getBase64Image(), type,dateOfEvent);

        return this.postRepository.save(post);
    }

    @Override


    public Post update(Long id, String title, String description,String shortDescription, MultipartFile image, Type type, Date dateOfEvent) throws IOException {
        Post post = this.findById(id);
        post.setTitle(title);
        post.setDescription(description);
        post.setShortDescription(shortDescription);


        if (image != null) {
            byte[] imageBytes = image.getBytes();
            String base64Image = String.format("data:%s;base64,%s", image.getContentType(), Base64.getEncoder().encodeToString(imageBytes));
            post.setBase64Image(base64Image);
        }

        post.setType(type);
        post.setDateOfEvent(dateOfEvent);
        return this.postRepository.save(post);
    }

    @Override
    public Post delete(Long id) {
        Post post = this.findById(id);
        this.postRepository.delete(post);
        return post;
    }

    @Override

    public Post findByTitle(String title) {
        return this.postRepository.findByTitle(title)
                .orElseThrow(InvalidPostIdException::new);
    }



    public Post click(Long id) {
        Post post = this.postRepository.findById(id).get();
          return  this.postRepository.save(post);
    }


   /* @Override
    public Post post_clicked(Long id,Integer postClicked){
       Post post = new Post(id,postClicked);
        return this.postRepository.save(post);
    }*/


}
