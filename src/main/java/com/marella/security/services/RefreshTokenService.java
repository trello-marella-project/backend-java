package com.marella.security.services;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.marella.exception.TokenRefreshException;
import com.marella.models.RefreshToken;
import com.marella.models.User;
import com.marella.repositories.RefreshTokenRepository;
import com.marella.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class RefreshTokenService {
    @Value("${marella.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private UserRepository userRepository;
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }
    @Transactional
    public int deleteByUser(User user) {
        return refreshTokenRepository.deleteByUser(user);
    }
    @Transactional
    public RefreshToken deleteToken(RefreshToken token){
        refreshTokenRepository.delete(token);
        return token;
    }
    @Transactional
    public void deleteExpiredTokensByUser(User user){
        List<RefreshToken> tokens = refreshTokenRepository.findAllById(List.of(user.getId()));
        for(RefreshToken token : tokens){
            if (token.getExpiryDate().compareTo(Instant.now()) < 0){
                refreshTokenRepository.delete(token);
            }
        }
    }
}