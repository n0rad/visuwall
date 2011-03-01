package com.jsmadja.wall.projectwall.persistence.dao.implementation;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jsmadja.wall.projectwall.persistence.dao.UserService;
import com.jsmadja.wall.projectwall.persistence.entity.User;

@Service
public class UserServiceImpl implements UserService {

	@Override
	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException, DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(User user) {
		// TODO Auto-generated method stub
		
	}

}
