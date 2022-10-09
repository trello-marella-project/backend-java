package com.marella.payload.response;

import com.marella.models.Role;

import java.util.List;
import java.util.Set;

public class OwnUserGetResponse {
    private String username;
    private String email;
    private List<Role> roles;
    private boolean isEnabled;

    public OwnUserGetResponse(String username, String email, Set<Role> roles, boolean isEnabled) {
        this.username = username;
        this.email = email;
        this.roles = roles.stream().toList();
        this.isEnabled = isEnabled;
    }

    @Override
    public String toString() {
        return "{" +
                "\"username\":\"" + username + '\"' +
                ",\"email\":\"" + email + '\"' +
                ",\"role\":\"" + roles.get(0) + '\"' +
                ",\"isEnabled\":" + isEnabled +
                '}';
    }
}
