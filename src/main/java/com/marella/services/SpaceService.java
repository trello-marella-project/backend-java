package com.marella.services;

import com.marella.models.Space;
import com.marella.models.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SpaceService {
    List<Space> getUserSpacesByLimitAndPage(User user, Long limit, Long page);

    List<Space> getUserPermittedSpacesByLimitAndPage(User user, Long limit, Long page);

    List<Space> getUserRecentSpacesByLimitAndPage(User user, Long limit, Long page);

    List<Space> getSearch(User user, Long limit, Long page, List<Long> tags_id, String search);
}
