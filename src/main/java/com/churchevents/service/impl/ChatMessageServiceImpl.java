package com.churchevents.service.impl;

import com.churchevents.model.ChatMessage;
import com.churchevents.model.ChatMessagePayload;
import com.churchevents.model.User;
import com.churchevents.model.enums.MessageStatus;
import com.churchevents.repository.ChatMessageRepository;
import com.churchevents.repository.UserRepository;
import com.churchevents.service.ChatMessageService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public ChatMessageServiceImpl(ChatMessageRepository chatMessageRepository,
                                  UserRepository userRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ChatMessagePayload save(ChatMessagePayload chatMessagePayload) {
        User sender = this.userRepository.findById(chatMessagePayload.getSenderId()).orElseThrow();
        User recipient = this.userRepository.findById(chatMessagePayload.getRecipientId()).orElseThrow();
        ChatMessage chatMessage = new ChatMessage(sender, recipient, chatMessagePayload.getContent(), chatMessagePayload.getTimestamp());
        this.chatMessageRepository.save(chatMessage);
        return new ChatMessagePayload(chatMessage.getId(), chatMessage.getSender().getEmail(),
                chatMessage.getRecipient().getEmail(),
                chatMessage.getContent(), chatMessage.getTimestamp());
    }

    @Override
    @Transactional
    public Slice<ChatMessagePayload> findChatMessages(String senderId, String recipientId, Pageable pageable) {
        User sender = this.userRepository.findById(senderId).orElseThrow();
        User recipient = this.userRepository.findById(recipientId).orElseThrow();
        Slice<ChatMessage> chatMessages = this.chatMessageRepository.findAllBySenderAndRecipientOrSenderAndRecipient(sender, recipient, recipient, sender, pageable);

        Slice<ChatMessage> chatMessagesToBeUpdated = chatMessages;
        boolean areThereNewMessagesInCurrentBatch = chatMessagesToBeUpdated.getContent().stream().anyMatch(chatMessage -> chatMessage.getMessageStatus().equals(MessageStatus.DELIVERED));
        int currentPage = pageable.getPageNumber();
        while (areThereNewMessagesInCurrentBatch) {
            List<ChatMessage> chatMessagesWithUpdatedStatus = chatMessagesToBeUpdated.getContent();
            chatMessagesWithUpdatedStatus.forEach(chatMessage -> chatMessage.setMessageStatus(MessageStatus.SEEN));
            this.chatMessageRepository.saveAll(chatMessagesWithUpdatedStatus);

            Pageable pageableCopy = PageRequest.of(++currentPage, pageable.getPageSize(), pageable.getSort());
            chatMessagesToBeUpdated = this.chatMessageRepository.findAllBySenderAndRecipientOrSenderAndRecipient(sender, recipient, recipient, sender, pageableCopy);
            areThereNewMessagesInCurrentBatch = chatMessagesToBeUpdated.getContent().stream().anyMatch(chatMessage -> chatMessage.getMessageStatus().equals(MessageStatus.DELIVERED));
        }

        return chatMessages.map(chatMessage -> new ChatMessagePayload(
                chatMessage.getId(), chatMessage.getSender().getEmail(), chatMessage.getRecipient().getEmail(),
                chatMessage.getContent(), chatMessage.getTimestamp()));
    }

    @Override
    public Long countNewMessages(String senderId, String recipientId) {
        User sender = this.userRepository.findById(senderId).orElseThrow();
        User recipient = this.userRepository.findById(recipientId).orElseThrow();
        return this.chatMessageRepository.countAllBySenderAndRecipientAndMessageStatus(sender, recipient, MessageStatus.DELIVERED);
    }

    @Override
    public void updateMessageStatus(Long chatMessageId) {
        ChatMessage chatMessage = this.chatMessageRepository.findById(chatMessageId).orElseThrow();
        chatMessage.setMessageStatus(MessageStatus.SEEN);
        this.chatMessageRepository.save(chatMessage);
    }
}
