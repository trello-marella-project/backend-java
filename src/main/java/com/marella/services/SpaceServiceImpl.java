package com.marella.services;

import com.marella.dbrequests.OffsetBasedPageRequest;
import com.marella.models.Space;
import com.marella.models.User;
import com.marella.payload.response.SpaceResponse;
import com.marella.repositories.SpaceRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SpaceServiceImpl implements SpaceService{
    private SpaceRepository spaceRepository;

    @Override
    public List<SpaceResponse> getUserSpacesByLimitAndPage(User user, int limit, int page) {
        Pageable pageable = new OffsetBasedPageRequest(limit, limit * page);
        return spaceRepository.findAllByUser(user.getId(), pageable);
    }

    @Override
    public List<SpaceResponse> getUserPermittedSpacesByLimitAndPage(User user, int limit, int page) {
        Pageable pageable = new OffsetBasedPageRequest(limit, limit * page);
        return spaceRepository.findPermittedByUser(user.getId(), pageable);
    }

    @Override
    public List<SpaceResponse> getUserRecentSpacesByLimitAndPage(User user, int limit, int page) {
        return null;
    }

    @Override
    public List<SpaceResponse> getSearch(User user, int limit, int page, List<Long> tags_id, String search) {
        return null;
    }
}
