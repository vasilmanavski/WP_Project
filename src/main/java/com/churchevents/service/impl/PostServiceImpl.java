package com.churchevents.service.impl;

import com.churchevents.model.Post;
import com.churchevents.model.Review;
import com.churchevents.model.User;
import com.churchevents.model.exceptions.InvalidPostIdException;
import com.churchevents.repository.PostRepository;
import com.churchevents.repository.ReviewRepository;
import com.churchevents.service.PostService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ReviewRepository reviewRepository;

    public PostServiceImpl(PostRepository postRepository, ReviewRepository reviewRepository) {
        this.postRepository = postRepository;
        this.reviewRepository = reviewRepository;
    }


    @Override
    public List<Post> listAllPosts() {
        List<Post> posts = this.postRepository.findAll();
        sortPosts(posts);

        return posts;
    }

    @Override
    public Post findById(Long id) {

       return this.postRepository.findById(id).orElseThrow(InvalidPostIdException::new);
    }



    @Override
    public Post create(String title, String description,String shortDescription, MultipartFile image,User user) throws IOException {

        Post post = new Post();
        if (image != null) {
            byte[] imageBytes = image.getBytes();
            String base64Image = String.format("data:%s;base64,%s", image.getContentType(), Base64.getEncoder().encodeToString(imageBytes));
            post.setBase64Image(base64Image);
        }


        post = new Post(title,description,shortDescription, post.getBase64Image(), user);

        return this.postRepository.save(post);
    }

    @Override


    public Post update(Long id, String title, String description, String shortDescription, MultipartFile image, User user) throws IOException {
        Post post = this.findById(id);
        post.setTitle(title);
        post.setDescription(description);
        post.setShortDescription(shortDescription);
        post.setUser(user);


        if (image != null) {
            byte[] imageBytes = image.getBytes();
            String base64Image = String.format("data:%s;base64,%s", image.getContentType(), Base64.getEncoder().encodeToString(imageBytes));
            post.setBase64Image(base64Image);
        }
        return this.postRepository.save(post);
    }

    @Override
    public Post delete(Long id) {
        Post post = this.findById(id);
        this.deleteByPost(post);
        this.postRepository.delete(post);
        return post;
    }

    @Override
    public void deleteByPost(Post post) {
        List<Review> reviews = this.reviewRepository.findAllByPost(post);

        for(Review review : reviews){
            review.setUser(null);
            review.setPost(null);
            this.reviewRepository.delete(review);
        }
    }

    @Override
    public Page<Post> findPaginated(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<Post> list;
        List<Post> posts = this.listAllPosts();
        if(posts.size() < startItem){
            list = Collections.emptyList();
        } else{
            int toIndex = Math.min(startItem + pageSize, posts.size());
            list = posts.subList(startItem, toIndex);
        }

        Page<Post> postPage = new PageImpl<Post>(list, PageRequest.of(currentPage,pageSize), posts.size());

        return postPage;
    }

    @Override

    public Post findByTitle(String title) {
        return this.postRepository.findByTitle(title)
                .orElseThrow(InvalidPostIdException::new);
    }

    @Override
    public List<Post> lastPosts() {
        List<Post> posts = this.postRepository.findAll();

        sortPosts(posts);
        return posts.stream().limit(3).collect(Collectors.toList());
    }

    public void sortPosts(List<Post> posts){
        posts.sort(Comparator.comparing(Post::getDateCreated).reversed());
    }



    public Post click(Long id) {
        Post post = this.postRepository.findById(id).get();
          return  this.postRepository.save(post);
    }

}
