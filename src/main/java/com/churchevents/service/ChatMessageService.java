package com.churchevents.service;

import com.churchevents.model.ChatMessage;
import com.churchevents.model.ChatMessagePayload;

public interface ChatMessageService {

    ChatMessage save(ChatMessagePayload chatMessagePayload);
}
