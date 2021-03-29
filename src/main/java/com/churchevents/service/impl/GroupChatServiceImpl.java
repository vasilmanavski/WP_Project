package com.churchevents.service.impl;

import com.churchevents.model.*;
import com.churchevents.model.exceptions.InvalidArgumentsException;
import com.churchevents.repository.GroupChatMessageRepository;
import com.churchevents.repository.GroupChatRepository;
import com.churchevents.repository.UserRepository;
import com.churchevents.repository.UsersGroupChatsRepository;
import com.churchevents.service.GroupChatService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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
    public List<ChatMessagePayload> findChatMessages(User user, Long groupChatId) {
        GroupChat groupChat = this.groupChatRepository.findById(groupChatId).orElseThrow();

        if (this.usersGroupChatsRepository.findByUserAndGroupChat(user, groupChat).isEmpty()) {
            throw new InvalidArgumentsException();
        }

        //todo: if user left group chat, return messages up to that date, else return "all" (pagination?)
        return this.groupChatMessageRepository.findAllByGroupChat(groupChat)
                .stream()
                .map(groupChatMessage -> new ChatMessagePayload(
                        groupChatMessage.getId(), groupChatMessage.getSender().getEmail(),
                        groupChatMessage.getGroupChat().getId().toString(), groupChatMessage.getContent(),
                        groupChatMessage.getTimestamp()
                ))
                .sorted(Comparator.comparing(ChatMessagePayload::getTimestamp).reversed())
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
    public List<String> userIdsThatBelongToGroupChat(User creator, GroupChat groupChat) {
        return this.usersGroupChatsRepository.findAllByGroupChat(groupChat)
                .stream()
                .map(usersGroupChats -> usersGroupChats.getUser().getEmail())
                .filter(userId -> !userId.equals(creator.getEmail()))
                .collect(Collectors.toList());
    }

    @Override
    public GroupChat findGroupById(Long groupChatId) {
        return this.groupChatRepository.findById(groupChatId).orElseThrow();
    }
}
