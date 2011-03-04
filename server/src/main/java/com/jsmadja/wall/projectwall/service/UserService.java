package com.jsmadja.wall.projectwall.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.jsmadja.wall.projectwall.domain.User;

public interface UserService extends UserDetailsService {

    void save(User user);

}
