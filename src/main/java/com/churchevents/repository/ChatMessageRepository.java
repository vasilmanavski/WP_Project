package com.churchevents.repository;

import com.churchevents.model.ChatMessage;
import com.churchevents.model.User;
import com.churchevents.model.enums.MessageStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Slice<ChatMessage> findAllBySenderAndRecipientOrSenderAndRecipient(User sender, User recipient, User recipientCopy, User senderCopy, Pageable pageable);

    Long countAllBySenderAndRecipientAndMessageStatus(User sender, User recipient, MessageStatus messageStatus);
}