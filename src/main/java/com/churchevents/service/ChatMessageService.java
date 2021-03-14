package com.churchevents.service;

import com.churchevents.model.ChatMessage;
import com.churchevents.model.ChatMessagePayload;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ChatMessageService {

    ChatMessage save(ChatMessagePayload chatMessagePayload);

    Slice<ChatMessagePayload> findChatMessages(String senderId, String recipientId, Pageable pageable);
}
