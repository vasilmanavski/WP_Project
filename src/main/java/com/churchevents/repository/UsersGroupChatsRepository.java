package com.churchevents.repository;

import com.churchevents.model.GroupChat;
import com.churchevents.model.User;
import com.churchevents.model.UsersGroupChats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersGroupChatsRepository extends JpaRepository<UsersGroupChats, Long> {

    List<UsersGroupChats> findAllByUser(User user);

    Optional<UsersGroupChats> findByUserAndGroupChat(User user, GroupChat groupChat);

    List<UsersGroupChats> findAllByGroupChat(GroupChat groupChat);
}
