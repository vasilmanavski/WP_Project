package com.churchevents.service.impl;

import com.churchevents.model.ChatMessage;
import com.churchevents.model.ChatMessagePayload;
import com.churchevents.model.User;
import com.churchevents.repository.ChatMessageRepository;
import com.churchevents.repository.UserRepository;
import com.churchevents.service.ChatMessageService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public ChatMessageServiceImpl(ChatMessageRepository chatMessageRepository,
                                  UserRepository userRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ChatMessage save(ChatMessagePayload chatMessagePayload) {
        User sender = this.userRepository.findById(chatMessagePayload.getSenderId()).orElseThrow();
        User recipient = this.userRepository.findById(chatMessagePayload.getRecipientId()).orElseThrow();
        return this.chatMessageRepository.save(new ChatMessage(sender, recipient, chatMessagePayload.getContent(), chatMessagePayload.getTimestamp()));
    }

    @Override
    public Slice<ChatMessagePayload> findChatMessages(String senderId, String recipientId, Pageable pageable) {
        User sender = this.userRepository.findById(senderId).orElseThrow();
        User recipient = this.userRepository.findById(recipientId).orElseThrow();
        Slice<ChatMessage> chatMessages = this.chatMessageRepository.findAllBySenderAndRecipientOrSenderAndRecipient(sender, recipient, recipient, sender, pageable);

        Slice<ChatMessagePayload> chatMessagePayloads = chatMessages.map(chatMessage -> new ChatMessagePayload(
                chatMessage.getSender().getEmail(), chatMessage.getRecipient().getEmail(),
                chatMessage.getContent(), chatMessage.getTimestamp()));
        return chatMessagePayloads;
    }
}
