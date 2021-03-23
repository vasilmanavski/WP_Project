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
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    static class SenderRecipientClass {

        private String id;

        private Date date;

        public SenderRecipientClass() { }

        public SenderRecipientClass(String id, Date date) {
            this.id = id;
            this.date = date;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }

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

    @Override
    public List<String> allUserEmails(User currentUser) {
        List<ChatMessage> chatMessagesSent = this.chatMessageRepository.findAllBySenderOrRecipient(currentUser, currentUser);

        String currentUserId = currentUser.getEmail();
        List<SenderRecipientClass> latestMessageBetweenUser = new ArrayList<>(
                chatMessagesSent.stream()
                        .map(chatMessage -> {
                            String senderId = chatMessage.getSender().getEmail();
                            String recipientId = chatMessage.getRecipient().getEmail();

                            String id = senderId.equals(currentUserId) ? senderId + " " + recipientId : recipientId + " " + senderId;

                            return new SenderRecipientClass(id, chatMessage.getTimestamp());
                        })
                        .collect(Collectors.toMap(SenderRecipientClass::getId, Function.identity(),
                                BinaryOperator.maxBy(Comparator.comparing(SenderRecipientClass::getDate))
                                )
                        )
                        .values()
        );

        List<String> allUsersWhoHaveMessagesWithTheCurrentUserSortedByLatestMessage = latestMessageBetweenUser.stream()
                .sorted(Comparator.comparing(SenderRecipientClass::getDate).reversed())
                .map(senderRecipientClass -> {
                    String[] userIdsPair = senderRecipientClass.getId().split("\\s");
                    String senderId = userIdsPair[0];
                    String recipientId = userIdsPair[1];

                    return senderId.equals(currentUserId) ? recipientId : senderId;
                })
                .collect(Collectors.toList());

        List<String> allOtherUsersWhoDontHaveMessagesWithTheCurrentUser = this.userRepository.findAll().stream()
                .map(User::getEmail)
                .filter(userId -> !userId.equals(currentUserId))
                .filter(userId -> !allUsersWhoHaveMessagesWithTheCurrentUserSortedByLatestMessage.contains(userId))
                .collect(Collectors.toList());

        allUsersWhoHaveMessagesWithTheCurrentUserSortedByLatestMessage.addAll(allOtherUsersWhoDontHaveMessagesWithTheCurrentUser);
        return allUsersWhoHaveMessagesWithTheCurrentUserSortedByLatestMessage;
    }
}