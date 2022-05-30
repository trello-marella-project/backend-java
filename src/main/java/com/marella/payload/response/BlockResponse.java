package com.marella.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BlockResponse {
    private Long blockId;
    private String name;
    private Long spaceId;

    @Override
    public String toString() {
        return "{" +
                "\"block_id\":" + blockId +
                ",\"name\":\"" + name + '\"' +
                ",\"spaceId\":" + spaceId +
                '}';
    }
}
