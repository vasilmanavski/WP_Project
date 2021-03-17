package com.churchevents.service;

import com.churchevents.model.ChatMessagePayload;
import com.churchevents.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface ChatMessageService {

    ChatMessagePayload save(ChatMessagePayload chatMessagePayload);

    Slice<ChatMessagePayload> findChatMessages(String senderId, String recipientId, Pageable pageable);

    Long countNewMessages(String senderId, String recipientId);

    void updateMessageStatus(Long chatMessageId);

    List<String> allUserEmails(User currentUser);
}
