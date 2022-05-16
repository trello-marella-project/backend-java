package com.marella.payload;

import com.marella.models.Tag;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpaceSearch {
    private Long id;
    private String name;
    private boolean isPublic;
    private String username;
    private List<Tag> tags;

    public SpaceSearch(Long id, String name, boolean isPublic, String username) {
        this.id = id;
        this.name = name;
        this.isPublic = isPublic;
        this.username = username;
    }
}
