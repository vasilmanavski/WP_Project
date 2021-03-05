package com.churchevents.service.impl;

import com.churchevents.model.ChatMessage;
import com.churchevents.model.ChatMessagePayload;
import com.churchevents.model.User;
import com.churchevents.model.exceptions.InvalidArgumentsException;
import com.churchevents.repository.ChatMessageRepository;
import com.churchevents.repository.UserRepository;
import com.churchevents.service.ChatMessageService;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        User sender = this.userRepository.findById(senderId).orElseThrow();
        User recipient = this.userRepository.findById(recipientId).orElseThrow();
        List<ChatMessage> sentMessages = this.chatMessageRepository.findAllBySenderAndRecipient(sender, recipient);
        List<ChatMessage> receivedMessages = this.chatMessageRepository.findAllBySenderAndRecipient(recipient, sender);

        return Stream.concat(sentMessages.stream(), receivedMessages.stream())
                .sorted(Comparator.comparing(ChatMessage::getTimestamp).reversed())
                .collect(Collectors.toList());
    }
}
