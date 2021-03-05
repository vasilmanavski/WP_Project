package com.churchevents.service;

import com.churchevents.model.ChatMessage;
import com.churchevents.model.ChatMessagePayload;

import java.util.List;

public interface ChatMessageService {

    ChatMessage save(ChatMessagePayload chatMessagePayload);

    List<ChatMessage> findChatMessages(String senderId, String recipientId);
}
