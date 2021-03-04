package com.churchevents.web;

import com.churchevents.model.ChatMessage;
import com.churchevents.model.User;
import com.churchevents.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Controller
public class ChatController {

    private final UserService userService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public ChatController(UserService userService,
                          SimpMessagingTemplate simpMessagingTemplate) {
        this.userService = userService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        ChatMessage tmp = chatMessage;
        simpMessagingTemplate.convertAndSendToUser(
                "test@email.com", "/queue/messages",
                new ChatMessage("Hello world!")
        );
    }

    @GetMapping("/chat-1")
    public String getChats(Model model) {
        model.addAttribute("allUsers", new ArrayList<String>());
        //model.addAttribute("allUsers", this.userService.allUsers());
        //model.addAttribute("allUsers", this.userService.allUsers().stream().map(User::getEmail).collect(Collectors.toList()));
        return "chat";
    }
}
