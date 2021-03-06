package com.marella.payload.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CardResponse {
    private Long cardId;
    private Long blockId;
    private String name;

    @Override
    public String toString() {
        return "{" +
                "\"card_id\":" + cardId +
                ",\"block_id\":" + blockId +
                ",\"name\":\"" + name + '\"' +
                '}';
    }
}
