package com.churchevents.service.impl;

import com.churchevents.model.*;
import com.churchevents.model.exceptions.InvalidArgumentsException;
import com.churchevents.repository.GroupChatMessageRepository;
import com.churchevents.repository.GroupChatRepository;
import com.churchevents.repository.UserRepository;
import com.churchevents.repository.UsersGroupChatsRepository;
import com.churchevents.service.GroupChatService;
import com.churchevents.util.OffsetBasedPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupChatServiceImpl implements GroupChatService {

    private final GroupChatRepository groupChatRepository;
    private final UsersGroupChatsRepository usersGroupChatsRepository;
    private final UserRepository userRepository;
    private final GroupChatMessageRepository groupChatMessageRepository;

    public GroupChatServiceImpl(GroupChatRepository groupChatRepository,
                                UsersGroupChatsRepository usersGroupChatsRepository,
                                UserRepository userRepository,
                                GroupChatMessageRepository groupChatMessageRepository) {
        this.groupChatRepository = groupChatRepository;
        this.usersGroupChatsRepository = usersGroupChatsRepository;
        this.userRepository = userRepository;
        this.groupChatMessageRepository = groupChatMessageRepository;
    }

    @Override
    @Transactional
    public GroupChat createGroupChat(String name, User creator, String[] invitedUserIds) {
        GroupChat groupChat = this.groupChatRepository.save(new GroupChat(name));
        this.usersGroupChatsRepository.save(new UsersGroupChats(creator, groupChat));

        List<UsersGroupChats> usersGroupChats = Arrays.stream(invitedUserIds)
                .map(userId -> {
                    User user = this.userRepository.findById(userId).orElseThrow();
                    return new UsersGroupChats(user, groupChat);
                })
                .collect(Collectors.toList());
        this.usersGroupChatsRepository.saveAll(usersGroupChats);

        return groupChat;
    }

    @Override
    public List<GroupChat> getGroupChatsForUser(User user) {
        return this.usersGroupChatsRepository.findAllByUser(user)
                .stream()
                .map(UsersGroupChats::getGroupChat)
                .collect(Collectors.toList());
    }

    @Override
    public ChatMessagePayload saveGroupMessage(ChatMessagePayload chatMessagePayload) {
        User user = this.userRepository.findById(chatMessagePayload.getSenderId()).orElseThrow();
        GroupChat groupChat = this.groupChatRepository.findById(Long.parseLong(chatMessagePayload.getRecipientId())).orElseThrow();

        if (this.usersGroupChatsRepository.findByUserAndGroupChat(user, groupChat).isEmpty()) {
            throw new InvalidArgumentsException();
        }

        GroupChatMessage groupChatMessage = new GroupChatMessage(
                chatMessagePayload.getContent(), chatMessagePayload.getTimestamp(), user, groupChat
        );
        this.groupChatMessageRepository.save(groupChatMessage);
        chatMessagePayload.setId(groupChatMessage.getId());
        return chatMessagePayload;
    }

    @Override
    public List<String> userIdsThatBelongToGroupChat(User user, GroupChat groupChat) {
        if (this.usersGroupChatsRepository.findByUserAndGroupChat(user, groupChat).isEmpty()) {
            throw new InvalidArgumentsException();
        }

        return this.usersGroupChatsRepository.findAllByGroupChat(groupChat)
                .stream()
                .map(usersGroupChats -> usersGroupChats.getUser().getEmail())
                .filter(userId -> !userId.equals(user.getEmail()))
                .collect(Collectors.toList());
    }

    @Override
    public GroupChat findGroupById(Long groupChatId) {
        return this.groupChatRepository.findById(groupChatId).orElseThrow();
    }

    @Override
    public Slice<ChatMessagePayload> findChatMessages(User user, Long groupChatId, Pageable pageable, Integer offset) {
        GroupChat groupChat = this.groupChatRepository.findById(groupChatId).orElseThrow();

        Optional<UsersGroupChats> usersGroupChatsOptional = this.usersGroupChatsRepository.findByUserAndGroupChat(user, groupChat);
        if (usersGroupChatsOptional.isEmpty()) {
            throw new InvalidArgumentsException();
        }

        Pageable pageableCopy = pageable;
        if (offset != null && offset != 0) {
            pageableCopy = new OffsetBasedPageRequest(offset, pageable.getPageSize(), pageable.getSort());
        }

        Slice<GroupChatMessage> groupChatMessages;
        Date dateWhenUserLeftGroupChat = usersGroupChatsOptional.get().getDateWhenUserLeftGroupChat();
        if (dateWhenUserLeftGroupChat != null) {
            groupChatMessages = this.groupChatMessageRepository.findAllByGroupChatAndTimestampBefore(groupChat, dateWhenUserLeftGroupChat, pageableCopy);
        } else {
            groupChatMessages = this.groupChatMessageRepository.findAllByGroupChat(groupChat, pageableCopy);
        }

        return groupChatMessages.map(groupChatMessage -> new ChatMessagePayload(
                groupChatMessage.getId(), groupChatMessage.getSender().getEmail(),
                groupChatMessage.getGroupChat().getId().toString(),
                groupChatMessage.getContent(), groupChatMessage.getTimestamp()
        ));
    }

    @Override
    public boolean userShouldReceiveMessage(String userId, GroupChat groupChat) {
        User user = this.userRepository.findById(userId).orElseThrow();
        UsersGroupChats userGroupChat = this.usersGroupChatsRepository.findByUserAndGroupChat(user, groupChat).orElseThrow();
        return userGroupChat.getDateWhenUserLeftGroupChat() == null;
    }
}
