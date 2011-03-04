package com.jsmadja.wall.projectwall.service.implementation;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jsmadja.wall.projectwall.domain.User;
import com.jsmadja.wall.projectwall.service.UserService;

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
