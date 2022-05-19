package com.marella.payload.request;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Map;

@Setter
@Getter
public class SpaceUpdateRequest {
    String name;
    Map<String, ArrayList<Long>> members;
    Map<String, ArrayList<String>> tags;
    boolean is_public;

    public void setIs_public(boolean is_public) {
        this.is_public = is_public;
    }
}
