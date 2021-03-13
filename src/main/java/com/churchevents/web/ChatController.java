package com.churchevents.web;

import com.churchevents.model.ChatMessage;
import com.churchevents.model.ChatMessagePayload;
import com.churchevents.service.ChatMessageService;
import com.churchevents.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
        ChatMessage chatMessage = this.chatMessageService.save(chatMessagePayload);
        this.simpMessagingTemplate.convertAndSendToUser(
                chatMessage.getRecipient().getEmail(), "/queue/messages",
                chatMessage
        );
    }

    @GetMapping("/chat-1")
    public String getChats(Model model) {
        model.addAttribute("allUsers", this.userService.allUserEmails());
        return "chat";
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<?> findChatMessages(@PathVariable String senderId,
                                              @PathVariable String recipientId) {
        return ResponseEntity
                .ok(this.chatMessageService.findChatMessages(senderId, recipientId));
    }

    @GetMapping("/messages/{senderId}/{recipientId}/paginated")
    public ResponseEntity<?> findChatMessagesPaginated(@PathVariable String senderId,
                                                       @PathVariable String recipientId,
                                                       Pageable pageable) {
        return ResponseEntity
                .ok(this.chatMessageService.findChatMessagesWithPagination(senderId, recipientId, pageable).getContent());
    }
}
