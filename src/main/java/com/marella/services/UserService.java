package com.marella.services;

import com.marella.models.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    User findUserByUsername(String username);

    User findUserById(Long id);

    User findUserByEmail(String email);

    List<User> findAllUsers();

    void createReport(User declarerUser, Long accusedUserId, String message);

    void updateUsername(User user, String username);

    void updatePassword(User user, String password);

    void updateBlockedStatus(User user, boolean isBlocked);
}
