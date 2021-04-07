package com.churchevents.repository;

import com.churchevents.model.GroupChat;
import com.churchevents.model.GroupChatMessage;
import com.churchevents.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface GroupChatMessageRepository extends JpaRepository<GroupChatMessage, Long> {

    List<GroupChatMessage> findAllByGroupChat(GroupChat groupChat);

    Slice<GroupChatMessage> findAllByGroupChatAndTimestampBefore(GroupChat groupChat, Date timestamp, Pageable pageable);

    Slice<GroupChatMessage> findAllByGroupChat(GroupChat groupChat, Pageable pageable);
}
