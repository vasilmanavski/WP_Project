package com.churchevents.web;

import com.churchevents.model.ChatMessage;
import com.churchevents.model.ChatMessagePayload;
import com.churchevents.model.User;
import com.churchevents.service.ChatMessageService;
import com.churchevents.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@Controller
public class ChatController {

    private final UserService userService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMessageService chatMessageService;

    public ChatController(UserService userService,
                          SimpMessagingTemplate simpMessagingTemplate,
                          ChatMessageService chatMessageService) {
        this.userService = userService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chatMessageService = chatMessageService;
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessagePayload chatMessagePayload) {
        ChatMessagePayload chatMessageSaved = this.chatMessageService.save(chatMessagePayload);
        this.simpMessagingTemplate.convertAndSendToUser(
                chatMessageSaved.getRecipientId(), "/queue/messages",
                chatMessageSaved
        );
    }

    @GetMapping("/chat")
    public String getChats(Model model, Authentication authentication) {
        User currentUser = (User)authentication.getPrincipal();
        model.addAttribute("allUsers", this.chatMessageService.allUserEmails(currentUser));
        return "chat";
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<?> findChatMessages(@PathVariable String senderId,
                                              @PathVariable String recipientId,
                                              Pageable pageable) {
        List<ChatMessagePayload> chatMessages;
        try {
            chatMessages = this.chatMessageService.findChatMessages(senderId, recipientId, pageable).getContent();
        } catch (Exception exception) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(chatMessages);
    }

    @GetMapping("/messages/{senderId}/count")
    public ResponseEntity<?> countNewMessages(@PathVariable String senderId,
                                              Authentication authentication) {
        String recipientId = ((User)authentication.getPrincipal()).getEmail();
        Long numberOfNewMessages;
        try {
            numberOfNewMessages = this.chatMessageService.countNewMessages(senderId, recipientId);
        } catch (Exception exception) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(numberOfNewMessages);
    }

    @PutMapping("/messages/{messageId}")
    public ResponseEntity<?> updateMessageStatus(@PathVariable Long messageId) {
        try {
            this.chatMessageService.updateMessageStatus(messageId);
        } catch (Exception exception) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(true);
    }
}
