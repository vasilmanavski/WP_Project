package com.churchevents.service;

import com.churchevents.model.User;
import com.churchevents.model.enums.Role;

import java.util.List;

public interface RoleService {
    List<Role> findAllRoles();
    void updateRole(String email, Role role);
}
