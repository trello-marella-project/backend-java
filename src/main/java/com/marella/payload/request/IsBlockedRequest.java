package com.marella.payload.request;

import lombok.Getter;

@Getter
public class IsBlockedRequest {
    private boolean is_blocked;

    public void setIs_blocked(boolean is_blocked) {
        this.is_blocked = is_blocked;
    }
}
