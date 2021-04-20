package com.churchevents.web;

import com.churchevents.model.Post;

import com.churchevents.model.User;
import com.churchevents.model.enums.Rating;
import com.churchevents.model.exceptions.EmailNotFoundException;
import com.churchevents.service.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import com.churchevents.service.AuthService;
import com.churchevents.service.PostService;
import com.churchevents.service.ReviewService;
import com.churchevents.service.UserService;

import java.io.IOException;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    public String showPosts(Model model,
                            @RequestParam("page") Optional<Integer> page,
                            @RequestParam("size") Optional<Integer> size) {
        Rating [] ratings = Rating.values();
        List<User> users = userService.listAllUsers();
        List<Post> posts = this.postService.listAllPosts();
        List<Post> lastPosts = this.postService.lastPosts();

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(3);
        Page<Post> postPage = postService.findPaginated(PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("postPage", postPage);

        int totalPages = postPage.getTotalPages();
        if(totalPages > 0){
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

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
        model.addAttribute("bodyContent", "cal");
        return "master-template";


    }


    @GetMapping("/posts/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String showAdd(Model model) {
        List<Post> posts = this.postService.listAllPosts();
        Rating [] ratings = Rating.values();



        model.addAttribute("posts",posts);
        model.addAttribute("rating",ratings);
        model.addAttribute("bodyContent", "posts_form");

        return "master-template";
    }

    @GetMapping("/posts/{id}/edit")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String showEdit(@PathVariable Long id, Model model) {
        Post post = this.postService.findById(id);

        model.addAttribute("post",post);

        model.addAttribute("bodyContent", "posts_form");

        return "master-template";

    }
    @PostMapping("/posts")
    public String create(@RequestParam String title,
                         @RequestParam String description,
                         @RequestParam String shortDescription,
                         @RequestParam MultipartFile image
                        ) throws IOException {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        String username = ((UserDetails)principal).getUsername();

        User user = this.userService.findByEmail(username)
                .orElseThrow(EmailNotFoundException::new);

       this.postService.create(title,description,shortDescription,image, user);
        return "redirect:/posts";
    }



    @PostMapping("/posts/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String title,
                         @RequestParam String description,

                         @RequestParam String shortDescription,

                         @RequestParam MultipartFile image) throws IOException {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        String username = ((UserDetails)principal).getUsername();

        User user = this.userService.findByEmail(username)
                .orElseThrow(EmailNotFoundException::new);


        this.postService.update(id,title,description,shortDescription,image,user);

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

        post.setPostClicked(post.getPostClicked() + 1);
        postService.click(id);

        model.addAttribute("clicked", post.getPostClicked());
        model.addAttribute("post",post);
        model.addAttribute("bodyContent", "readMore.html");

        return "master-template";
    }
}
