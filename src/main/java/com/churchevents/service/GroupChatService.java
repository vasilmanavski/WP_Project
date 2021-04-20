package com.churchevents.service;

import com.churchevents.model.ChatMessagePayload;
import com.churchevents.model.GroupChat;
import com.churchevents.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface GroupChatService {

    GroupChat createGroupChat(String name, User creator, String[] invitedUserIds);

    List<GroupChat> getGroupChatsForUser(User user);

    ChatMessagePayload saveGroupMessage(ChatMessagePayload chatMessagePayload);

    List<String> userIdsThatBelongToGroupChat(User user, GroupChat groupChat);

    GroupChat findGroupById(Long groupChatId);

    Slice<ChatMessagePayload> findChatMessages(User user, Long groupChatId, Pageable pageable, Integer offset);

    boolean userShouldReceiveMessage(String userId, GroupChat groupChat);
}
