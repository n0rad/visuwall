package com.jsmadja.wall.projectwall.persistence.dao;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.jsmadja.wall.projectwall.persistence.entity.User;

public interface UserService extends UserDetailsService {

	void save(User user);
	
}
