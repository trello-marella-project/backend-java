package com.marella.payload.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpaceResponse {
    private Long id;
    private String name;
    private boolean isPublic;
    private String username;

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ",\"name\":\"" + name + '\"' +
                ",\"isPublic\":" + isPublic +
                ",\"username\":\"" + username + '\"' +
                '}';
    }
}
