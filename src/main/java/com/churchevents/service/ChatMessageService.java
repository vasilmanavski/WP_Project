package com.churchevents.service;

import com.churchevents.model.ChatMessage;
import com.churchevents.model.ChatMessagePayload;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ChatMessageService {

    ChatMessagePayload save(ChatMessagePayload chatMessagePayload);

    Slice<ChatMessagePayload> findChatMessages(String senderId, String recipientId, Pageable pageable);

    Long countNewMessages(String senderId, String recipientId);

    void updateMessageStatus(Long chatMessageId);
}
