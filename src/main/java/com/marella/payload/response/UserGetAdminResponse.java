package com.marella.payload.response;

import com.marella.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class UserGetAdminResponse {
    class TempUser{
        private Long userId;
        private String username;
        private String email;
        private boolean isBlocked;

        public TempUser(Long userId, String username, String email, boolean isBlocked) {
            this.userId = userId;
            this.username = username;
            this.email = email;
            this.isBlocked = isBlocked;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"user_id\":" + userId +
                    ",\"username\":\"" + username + '\"' +
                    ",\"email\":\"" + email + '\"' +
                    ",\"is_blocked\":" + isBlocked +
                    '}';
        }
    }

    private List<TempUser> users;

    public UserGetAdminResponse(List<User> users) {
        this.users = new ArrayList<>();
        for(User user : users){
            this.users.add(new TempUser(user.getId(), user.getUsername(), user.getEmail(), user.getBlocked()));
        }
    }

    @Override
    public String toString() {
        StringBuilder response = new StringBuilder();
        String prefix = "{\"users\":[";
        if(users.isEmpty()) response.append(prefix);
        for(TempUser user : users){
            response.append(prefix);
            response.append(user.toString());
            prefix = ",";
        }
        return response.append("]}").toString();
    }
}
