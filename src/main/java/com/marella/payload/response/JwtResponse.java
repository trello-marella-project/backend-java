package com.marella.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String refreshToken;
    private String username;
    private String email;
    private List<String> roles;
    private boolean isEnabled;

    @Override
    public String toString() {
        return "{" +
                "\"accessToken\":\"" + token + '\"' +
                ",\"refreshToken\":\"" + refreshToken + '\"' +
                ",\"user\":{" +
                "\"username\":\"" + username + '\"' +
                ",\"email\":\"" + email + '\"' +
                ",\"role\":\"" + roles.get(0) + '\"' +
                ",\"isEnabled\":" + isEnabled +
                "}}";
    }

    public JwtResponse(String accessToken,
                       String refreshToken,
                       String username,
                       String email,
                       List<String> roles,
                       boolean isEnabled) {
        this.token = accessToken;
        this.refreshToken = refreshToken;
        this.isEnabled = isEnabled;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}