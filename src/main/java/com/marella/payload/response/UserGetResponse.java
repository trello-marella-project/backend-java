package com.marella.payload.response;

public class UserGetResponse {
    private Long userId;
    private String username;
    private String email;

    public UserGetResponse(Long userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    @Override
    public String toString() {
        return "{" +
                "\"user_id\":" + userId +
                ",\"username\":\"" + username + '\"' +
                ",\"email\":\"" + email + '\"' +
                '}';
    }
}
