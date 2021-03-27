package com.churchevents.web;

import com.churchevents.model.Post;

import com.churchevents.model.Subscriber;
import com.churchevents.model.User;
import com.churchevents.model.enums.Rating;
import com.churchevents.model.enums.Type;
import com.churchevents.service.*;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.*;

import com.churchevents.model.User;
import com.churchevents.model.enums.Rating;
import com.churchevents.model.enums.Type;
import com.churchevents.service.AuthService;
import com.churchevents.service.PostService;
import com.churchevents.service.ReviewService;
import com.churchevents.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Controller
public class PostsController {

    private final PostService postService;
    private final UserService userService;
    private final ReviewService reviewService;

    private final EmailSenderService emailSenderService;
    private final SubscribersService subscribersService;
    private final AuthService authService;



    public PostsController(PostService postService, UserService userService, ReviewService reviewService, AuthService authService,EmailSenderService emailSenderService, SubscribersService subscribersService){
        this.postService = postService;
        this.userService = userService;
        this.reviewService = reviewService;
        this.authService = authService;
        this.emailSenderService = emailSenderService;
        this.subscribersService = subscribersService;

    }

    @GetMapping({"/","/posts"})
    public String showPosts(Model model) {
        Rating [] ratings = Rating.values();
        List<User> users = userService.listAllUsers();
        List<Post> posts = this.postService.listAllPosts();
        List<Post> lastPosts = this.postService.lastPosts();

        model.addAttribute("rating",ratings);
        model.addAttribute("posts",posts);
        model.addAttribute("lastPosts", lastPosts);
        model.addAttribute("user",users);
        model.addAttribute("bodyContent", "list_posts");
        return "master-template";


    }
    @GetMapping("/calendar")
    public String showCalendar(Model model) {
        Rating [] ratings = Rating.values();
        List<User> users = userService.listAllUsers();
        List<Post> posts = this.postService.listAllPosts();
        model.addAttribute("rating",ratings);
        model.addAttribute("posts",posts);
        model.addAttribute("user",users);
        return "cal.html";


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

        model.addAttribute("bodyContent", "posts_form");

        return "master-template";

    }
    @PostMapping("/posts")
    public String create(@RequestParam String title,
                         @RequestParam String description,
                         @RequestParam String shortDescription,
                         @RequestParam MultipartFile image,
                         @RequestParam Type type,
                         @RequestParam Date dateOfEvent) throws IOException, SQLException {

       this.postService.create(title,description,shortDescription,image,type,dateOfEvent);
        return "redirect:/posts";
    }



    @PostMapping("/posts/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String title,
                         @RequestParam String description,

                         @RequestParam String shortDescription,

                         @RequestParam MultipartFile image,
                         @RequestParam Type type,
                         @RequestParam Date dateOfEvent) throws IOException {


        this.postService.update(id,title,description,shortDescription,image,type,dateOfEvent);

        return "redirect:/posts";
    }

    @PostMapping("/posts/{id}/review")
    public String review(@PathVariable Long id,
                         @RequestParam Rating rating,

                         @RequestParam String email
    ) {

       User user = userService.findByEmail(email).get();
       Post post = this.postService.findById(id);

        this.reviewService.create(rating,user,post);
        return "redirect:/posts";
    }


    @PostMapping("/posts/{id}/delete")
    public String delete(@PathVariable Long id) {
       this.postService.delete(id);
        return "redirect:/posts";
    }

    @GetMapping("posts/{id}/readMore")
    public String readMore(@PathVariable Long id, Model model){


        Post post = postService.findById(id);

        int i = post.getPostClicked();
        i = i+1;
        post.setPostClicked(i);
        postService.click(id);
        System.out.println(post.getPostClicked());
        Type [] type = Type.values();

        model.addAttribute("clicked", post.getPostClicked());
        model.addAttribute("type",type);
        model.addAttribute("post",post);

        return "readMore.html";
    }

}
