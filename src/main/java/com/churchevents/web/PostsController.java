package com.churchevents.web;

import com.churchevents.model.Post;
import com.churchevents.model.Review;
import com.churchevents.model.User;
import com.churchevents.model.enums.Rating;
import com.churchevents.model.enums.Type;
import com.churchevents.service.PostService;
import com.churchevents.service.ReviewService;
import com.churchevents.service.UserService;

import javassist.bytecode.ByteArray;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Controller
public class PostsController {

    private final PostService postService;
    private final UserService userService;
    private final ReviewService reviewService;

    public PostsController(PostService postService, UserService userService, ReviewService reviewService){
        this.postService = postService;
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @GetMapping({"/","/posts"})
    public String showPosts(Model model) {
        Rating [] ratings = Rating.values();
        List<User> users = userService.listAllUsers();
        List<Post> posts = this.postService.listAllPosts();
        model.addAttribute("rating",ratings);
        model.addAttribute("posts",posts);
        model.addAttribute("user",users);
        return "list_posts.html";

    }

    @GetMapping("/posts/add")
    public String showAdd(Model model) {
        List<Post> posts = this.postService.listAllPosts();
        Type [] type = Type.values();
        Rating [] ratings = Rating.values();
        model.addAttribute("posts",posts);
        model.addAttribute("type",type);
        model.addAttribute("rating",ratings);
        return "posts_form.html";
    }

    @GetMapping("/posts/{id}/edit")
    public String showEdit(@PathVariable Long id, Model model) {
        Type [] type = Type.values();
        model.addAttribute("type",type);

        Post post = this.postService.findById(id);

        model.addAttribute("post",post);

        return "posts_form.html";
    }
    @PostMapping("/posts")
    public String create(@RequestParam String title,
                         @RequestParam String description,
                         @RequestParam MultipartFile image,
                         @RequestParam Type type,
                         @RequestParam Date dateOfEvent) throws IOException, SQLException {

       this.postService.create(title,description,image,type,dateOfEvent);
        return "redirect:/posts";
    }

    @PostMapping("/posts/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String title,
                         @RequestParam String description,
                         @RequestParam MultipartFile image,
                         @RequestParam Type type,
                         @RequestParam Date dateOfEvent) throws IOException {

        this.postService.update(id,title,description,image,type,dateOfEvent);
        return "redirect:/posts";
    }

    @PostMapping("/posts/{id}/review")
    public String review(@PathVariable Long id,
                         @RequestParam Rating rating,
                         @RequestParam String email,
                         Model model
                         ) {
        List<User> users = userService.listAllUsers();
        Rating [] ratings = Rating.values();
        Post post = this.postService.findById(id);
        User user = this.userService.findByEmail(email);
        model.addAttribute("user",users);
        model.addAttribute("rating",ratings);
        this.reviewService.create(rating,user,post);
        return "redirect:/posts";
    }


    @PostMapping("/posts/{id}/delete")
    public String delete(@PathVariable Long id) {
       this.postService.delete(id);
        return "redirect:/posts";
    }
}
