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
    private boolean isPublic;

    public void setPublic(boolean is_public) {
        isPublic = is_public;
    }

    public boolean getPublic() {
        return isPublic;
    }
}
