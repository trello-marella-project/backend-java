package com.marella.payload.response;

import com.marella.models.Permission;
import com.marella.models.Space;
import com.marella.models.Tag;
import com.marella.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetSpaceResponse {
    Space space;

    @Override
    public String toString() {
        StringBuilder response = new StringBuilder();
        response.append(String.format("{\"name\":\"%s\",", space.getName()));

        String prefix = "\"members\":[";
        if (space.getPermissions().isEmpty()) response.append(prefix);
        else
            for (Permission permission : space.getPermissions()) {
                response.append(prefix);
                response.append(permission.getUser().getId());
                prefix = ",";
            }
        response.append("],");

        prefix = "\"tags\":[";
        if (space.getTags().isEmpty()) response.append(prefix);
        else
            for (Tag tag : space.getTags()) {
                response.append(prefix);
                response.append(String.format("\"%s\"", tag.getName()));
                prefix = ",";
            }
        response.append("],");

        response.append(String.format("\"is_public\":%s}", space.isPublic()));
        return response.toString();
    }
}
