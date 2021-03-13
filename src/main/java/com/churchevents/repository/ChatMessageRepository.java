package com.churchevents.repository;

import com.churchevents.model.ChatMessage;
import com.churchevents.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findAllBySenderAndRecipient(User sender, User recipient);

    Slice<ChatMessage> findAllBySenderAndRecipientOrSenderAndRecipient(User sender, User recipient, User recipientCopy, User senderCopy, Pageable pageable);
}