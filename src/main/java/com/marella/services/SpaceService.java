package com.marella.services;

import com.marella.models.Space;
import com.marella.models.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SpaceService {
    List<Space> getUserSpacesByLimitAndPage(User user, int limit, int page);

    List<Space> getUserPermittedSpacesByLimitAndPage(User user, int limit, int page);

    List<Space> getUserRecentSpacesByLimitAndPage(User user, int limit, int page);

    List<Space> getSearch(User user, int limit, int page, List<Long> tags_id, String search);
}
