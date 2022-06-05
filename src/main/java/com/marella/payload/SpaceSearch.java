package com.marella.payload;

import com.marella.models.Space;
import com.marella.models.Tag;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SpaceSearch {
    @AllArgsConstructor
    class TempSpace{
        private Long id;
        private String name;
        private String username;
        private List<Tag> tags;

        @Override
        public String toString() {
            StringBuilder response = new StringBuilder();
            response.append(String.format("{\"space_id\":%d", id));
            response.append(String.format(",\"name\":\"%s\"", name));
            response.append(String.format(",\"username\":\"%s\"", username));
            String prefix = ",\"tags\":[";
            if(tags.isEmpty()) response.append(prefix);
            for(Tag tag : tags){
                response.append(prefix);
                response.append(String.format("\"%s\"", tag.getName()));
                prefix = ",";
            }
            return response.append("]}").toString();
        }
    }

    private List<TempSpace> spaces;

    public SpaceSearch(List<Space> spaces) {
        this.spaces = new ArrayList<>();
        for(Space space : spaces)
            this.spaces.add(new TempSpace(space.getId(), space.getName(), space.getUser().getUsername(), space.getTags()));
    }

    @Override
    public String toString() {
        StringBuilder response = new StringBuilder();
        String prefix = "{\"spaces\":[";
        if(spaces.isEmpty()) response.append(prefix);
        for(TempSpace space : spaces){
            response.append(prefix);
            response.append(space.toString());
            prefix = ",";
        }
        return response.append("]}").toString();
    }
}
