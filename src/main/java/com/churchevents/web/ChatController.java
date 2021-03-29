package com.churchevents.web;

import com.churchevents.model.ChatMessagePayload;
import com.churchevents.model.GroupChat;
import com.churchevents.model.User;
import com.churchevents.service.ChatMessageService;
import com.churchevents.service.GroupChatService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMessageService chatMessageService;
    private final GroupChatService groupChatService;

    public ChatController(SimpMessagingTemplate simpMessagingTemplate,
                          ChatMessageService chatMessageService,
                          GroupChatService groupChatService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chatMessageService = chatMessageService;
        this.groupChatService = groupChatService;
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

    ///////////////////////////// GroupChat /////////////////////////////

    @GetMapping("/chat/groupChats")
    public ResponseEntity<?> getGroupChats(Authentication authentication) {
        User currentUser = (User)authentication.getPrincipal();
        return ResponseEntity.ok(this.groupChatService.getGroupChatsForUser(currentUser));
    }

    @PostMapping("/chat/createGroupChat")
    public ResponseEntity<?> createGroupChat(@RequestParam String groupChatName,
                                             @RequestParam String[] invitedUserIds,
                                             Authentication authentication) {
        User currentUser = (User)authentication.getPrincipal();
        GroupChat groupChat;
        try {
            groupChat = this.groupChatService.createGroupChat(groupChatName, currentUser, invitedUserIds);
        } catch (Exception exception) {
            return ResponseEntity.notFound().build();
        }

        List<String> users = this.groupChatService.userIdsThatBelongToGroupChat(currentUser, groupChat);
        users.forEach(user -> this.simpMessagingTemplate.convertAndSendToUser(
                user, "/queue/groupChat", groupChat
        ));
        return ResponseEntity.ok(groupChat);
    }

    @GetMapping("/group/{userId}/{groupChatId}")
    public ResponseEntity<?> getGroupChatMessages(@PathVariable String userId,
                                                  @PathVariable Long groupChatId,
                                                  Authentication authentication) {
        User authenticatedUser = (User)authentication.getPrincipal();
        if (!userId.equals(authenticatedUser.getEmail())) {
            return ResponseEntity.badRequest().build();
        }

        List<ChatMessagePayload> chatMessages;
        try {
            chatMessages = this.groupChatService.findChatMessages(authenticatedUser, groupChatId);
        } catch (Exception exception) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(chatMessages);
    }

    @MessageMapping("/group")
    public void groupChatCreated(@Payload ChatMessagePayload chatMessagePayload,
                                 Authentication authentication) {
        ChatMessagePayload chatMessageSaved = this.groupChatService.saveGroupMessage(chatMessagePayload);
        User currentUser = (User)authentication.getPrincipal();

        GroupChat groupChat;
        List<String> users;
        try {
            groupChat = this.groupChatService.findGroupById(Long.parseLong(chatMessagePayload.getRecipientId()));
            users = this.groupChatService.userIdsThatBelongToGroupChat(currentUser, groupChat);
        } catch (Exception exception) {
            return;
        }

        users.forEach(user -> this.simpMessagingTemplate.convertAndSendToUser(
                user, "/queue/groupChat", chatMessageSaved
        ));
    }
}
