package com.marella.payload.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SpaceCreationRequest {
    private String name;
    private List<Long> members;
    private List<String> tags;
    private boolean is_public;

    public void setIs_public(boolean is_public) {
        this.is_public = is_public;
    }
}
