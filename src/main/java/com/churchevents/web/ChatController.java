package com.churchevents.web;

import com.churchevents.model.ChatMessagePayload;
import com.churchevents.model.User;
import com.churchevents.service.ChatMessageService;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMessageService chatMessageService;

    public ChatController(SimpMessagingTemplate simpMessagingTemplate,
                          ChatMessageService chatMessageService) {
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

//    @MessageMapping("/seen")
//    public void sendSeenStatus() {
//
//    }

    @GetMapping("/chat")
    public String getChats(Model model, Authentication authentication) {
        User currentUser = (User)authentication.getPrincipal();
        model.addAttribute("allUsers", this.chatMessageService.allUserEmails(currentUser));
        return "chat";
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<?> findChatMessages(@PathVariable String senderId,
                                              @PathVariable String recipientId,
                                              @RequestParam(required = false) Integer offset,
                                              Pageable pageable,
                                              Authentication authentication) {
        String authenticatedUserId = ((User)authentication.getPrincipal()).getEmail();
        if (!senderId.equals(authenticatedUserId)) {
            return ResponseEntity.badRequest().build();
        }
        List<ChatMessagePayload> chatMessages;
        try {
            chatMessages = this.chatMessageService.findChatMessages(senderId, recipientId, pageable, offset).getContent();
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
    public ResponseEntity<?> updateMessageStatus(@PathVariable Long messageId,
                                                 Authentication authentication) {
        String authenticatedRecipientId = ((User)authentication.getPrincipal()).getEmail();
        try {
            this.chatMessageService.updateMessageStatus(messageId, authenticatedRecipientId);
        } catch (Exception exception) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(true);
    }
}
