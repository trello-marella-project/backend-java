package com.marella.services;

import com.marella.models.Report;
import com.marella.models.User;
import com.marella.repositories.ReportRepository;
import com.marella.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private ReportRepository reportRepository;

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException(String.format("user with username: %s does not exist", username))
        );
    }

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

    @Override
    public void createReport(User declarerUser, Long accusedUserId, String message) {
        if(declarerUser.getId().equals(accusedUserId))
            throw new IllegalArgumentException("declarer and accused user is the same");
        User accusedUser = findUserById(accusedUserId);
        reportRepository.save(new Report(declarerUser, accusedUser, message));
    }

    public User findUserById(Long userId){
        return userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException(String.format("user with id: %d does not exist", userId))
        );
    }
}
