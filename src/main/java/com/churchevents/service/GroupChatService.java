package com.churchevents.service;

import com.churchevents.model.ChatMessagePayload;
import com.churchevents.model.GroupChat;
import com.churchevents.model.User;

import java.util.List;

public interface GroupChatService {

    GroupChat createGroupChat(String name, User creator, String[] invitedUserIds);

    List<GroupChat> getGroupChatsForUser(User user);

    List<ChatMessagePayload> findChatMessages(User user, Long groupChatId);

    ChatMessagePayload saveGroupMessage(ChatMessagePayload chatMessagePayload);
}
