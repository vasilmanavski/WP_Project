package com.churchevents.service.impl;

import com.churchevents.model.ChatMessage;
import com.churchevents.model.ChatMessagePayload;
import com.churchevents.model.User;
import com.churchevents.repository.ChatMessageRepository;
import com.churchevents.repository.UserRepository;
import com.churchevents.service.ChatMessageService;
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
}
