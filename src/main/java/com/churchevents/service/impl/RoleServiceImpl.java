package com.churchevents.service.impl;

import com.churchevents.model.User;
import com.churchevents.model.enums.Role;
import com.churchevents.model.exceptions.EmailNotFoundException;
import com.churchevents.model.exceptions.InvalidEmailOrPasswordException;
import com.churchevents.model.exceptions.InvalidEmailOrRoleException;
import com.churchevents.repository.UserRepository;
import com.churchevents.service.RoleService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RoleServiceImpl implements RoleService {

    private final UserRepository userRepository;

    public RoleServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<Role> findAllRoles(){
        return Stream.of(Role.values()).collect(Collectors.toList());
    }

    @Override
    public void updateRole(String email, Role role) {

        if(email.isEmpty() || email==null || role==null){
            throw new InvalidEmailOrRoleException();
        }

        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException());
        user.setRole(role);
        this.userRepository.save(user);

    }
}
