package com.churchevents.service;

import com.churchevents.model.ChatMessage;
import com.churchevents.model.ChatMessagePayload;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface ChatMessageService {

    ChatMessage save(ChatMessagePayload chatMessagePayload);

    List<ChatMessage> findChatMessages(String senderId, String recipientId);

    Slice<ChatMessage> findChatMessagesWithPagination(String senderId, String recipientId, Pageable pageable);
}
