package com.churchevents.service.impl;

import com.churchevents.model.ChatMessage;
import com.churchevents.model.ChatMessagePayload;
import com.churchevents.model.User;
import com.churchevents.model.enums.MessageStatus;
import com.churchevents.repository.ChatMessageRepository;
import com.churchevents.repository.UserRepository;
import com.churchevents.service.ChatMessageService;
import com.churchevents.util.OffsetBasedPageRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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
        chatMessagePayload.setId(chatMessage.getId());
        return chatMessagePayload;
    }

    @Override
    @Transactional
    public Slice<ChatMessagePayload> findChatMessages(String senderId, String recipientId, Pageable pageable, Integer offset) {
        Pageable pageableCopy = pageable;
        if (offset != null && offset != 0) {
            pageableCopy = new OffsetBasedPageRequest(offset, pageable.getPageSize(), pageable.getSort());
        }
        User sender = this.userRepository.findById(senderId).orElseThrow();
        User recipient = this.userRepository.findById(recipientId).orElseThrow();
        Slice<ChatMessage> chatMessages = this.chatMessageRepository.findAllBySenderAndRecipientOrSenderAndRecipient(sender, recipient, recipient, sender, pageableCopy);

        Slice<ChatMessage> chatMessagesToBeUpdated = chatMessages;
        List<ChatMessage> chatMessagesWithUpdatedStatus =
                chatMessagesToBeUpdated.getContent()
                        .stream()
                        .filter(chatMessage -> chatMessage.getRecipient().getEmail().equals(sender.getEmail())
                                && chatMessage.getMessageStatus().equals(MessageStatus.DELIVERED))
                        .collect(Collectors.toList());

        while (chatMessagesWithUpdatedStatus.size() > 0) {
            chatMessagesWithUpdatedStatus.forEach(chatMessage -> chatMessage.setMessageStatus(MessageStatus.SEEN));
            this.chatMessageRepository.saveAll(chatMessagesWithUpdatedStatus);

            if (!chatMessagesToBeUpdated.hasNext()) {
                break;
            }

            if (offset != null && offset != 0) {
                pageableCopy = new OffsetBasedPageRequest((int)(pageableCopy.getOffset() + pageableCopy.getPageSize()), pageableCopy.getPageSize(), pageableCopy.getSort());
            } else {
                pageableCopy = PageRequest.of(pageableCopy.getPageNumber() + 1, pageableCopy.getPageSize(), pageableCopy.getSort());
            }

            chatMessagesToBeUpdated = this.chatMessageRepository.findAllBySenderAndRecipientOrSenderAndRecipient(sender, recipient, recipient, sender, pageableCopy);
            chatMessagesWithUpdatedStatus =
                    chatMessagesToBeUpdated.getContent()
                            .stream()
                            .filter(chatMessage -> chatMessage.getRecipient().getEmail().equals(sender.getEmail())
                                    && chatMessage.getMessageStatus().equals(MessageStatus.DELIVERED))
                            .collect(Collectors.toList());
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
    public void updateMessageStatus(Long chatMessageId, String authenticatedUserId) {
        ChatMessage chatMessage = this.chatMessageRepository.findById(chatMessageId).orElseThrow();
        if (!chatMessage.getRecipient().getEmail().equals(authenticatedUserId)) {
            throw new IllegalArgumentException();
        }
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
                .map(senderRecipientClass -> senderRecipientClass.getId().split("\\s")[1])
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
