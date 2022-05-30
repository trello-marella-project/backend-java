package com.marella.services;

import com.marella.models.Report;
import com.marella.models.User;
import com.marella.repositories.ReportRepository;
import com.marella.repositories.UserRepository;
import com.marella.security.services.RefreshTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private PasswordEncoder encoder;

    private UserRepository userRepository;
    private ReportRepository reportRepository;

    private RefreshTokenService refreshTokenService;

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException(String.format("user with username: %s does not exist", username))
        );
    }

    @Override
    public User findUserById(Long id){
        return userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(String.format("user with id: %d does not exist", id))
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

    @Override
    public void updateUsername(User user, String username) {
        user.setUsername(username);
        userRepository.save(user);
    }

    @Override
    public void updatePassword(User user, String password) {
        refreshTokenService.deleteByUser(user);
        user.setPassword(encoder.encode(password));
        userRepository.save(user);
    }

}
