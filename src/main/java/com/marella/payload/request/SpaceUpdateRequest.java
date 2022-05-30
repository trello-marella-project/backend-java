package com.marella.payload.request;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Map;

@Setter
@Getter
public class SpaceUpdateRequest {
    private String name;
    private Map<String, ArrayList<Long>> members;
    private Map<String, ArrayList<String>> tags;
    private boolean is_public;

    public void setIs_public(boolean is_public) {
        this.is_public = is_public;
    }
}
