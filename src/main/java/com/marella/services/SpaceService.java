package com.marella.services;

import com.marella.models.User;
import com.marella.payload.SpaceSearch;
import com.marella.payload.response.SpaceResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SpaceService {
    List<SpaceResponse> getUserSpacesByLimitAndPage(User user, int limit, int page);

    List<SpaceResponse> getUserPermittedSpacesByLimitAndPage(User user, int limit, int page);

    List<SpaceResponse> getUserRecentSpacesByLimitAndPage(User user, int limit, int page);

    List<SpaceSearch> getSearch(User user, int limit, int page, List<Long> tags_id, String search);

    void createSpace(User owner, String name, List<Long> members, List<String> tags, boolean isPublic);

}
