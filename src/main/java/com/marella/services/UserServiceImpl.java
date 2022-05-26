package com.marella.services;

import com.marella.models.User;
import com.marella.payload.response.UserGetResponse;
import com.marella.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException(String.format("user with email: %s does not exist", email))
        );
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}
