package com.marella.services;

import com.marella.models.Space;
import com.marella.models.User;
import com.marella.repositories.SpaceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SpaceServiceImpl implements SpaceService{
    private SpaceRepository spaceRepository;

    @Override
    public List<Space> getUserSpacesByLimitAndPage(User user, Long limit, Long page) {
        return null;
    }

    @Override
    public List<Space> getUserPermittedSpacesByLimitAndPage(User user, Long limit, Long page) {
        return null;
    }

    @Override
    public List<Space> getUserRecentSpacesByLimitAndPage(User user, Long limit, Long page) {
        return null;
    }

    @Override
    public List<Space> getSearch(User user, Long limit, Long page, List<Long> tags_id, String search) {
        return null;
    }
}
