package com.marella.services;

import com.marella.models.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    User findUserByEmail(String email);

    List<User> findAllUsers();
}
