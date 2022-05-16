package com.marella.services;

import com.marella.models.Space;
import com.marella.models.User;
import com.marella.payload.response.SpaceResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SpaceService {
    List<SpaceResponse> getUserSpacesByLimitAndPage(User user, int limit, int page);

    List<SpaceResponse> getUserPermittedSpacesByLimitAndPage(User user, int limit, int page);

    List<SpaceResponse> getUserRecentSpacesByLimitAndPage(User user, int limit, int page);

    List<SpaceResponse> getSearch(User user, int limit, int page, List<Long> tags_id, String search);
}
