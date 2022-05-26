package com.marella.services;

import com.marella.models.Space;
import com.marella.models.User;
import com.marella.payload.SpaceSearch;
import com.marella.payload.response.SpaceResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public interface SpaceService {
    List<SpaceResponse> getUserSpacesByLimitAndPage(User user, int limit, int page);

    List<SpaceResponse> getUserPermittedSpacesByLimitAndPage(User user, int limit, int page);

    List<SpaceResponse> getUserRecentSpacesByLimitAndPage(User user, int limit, int page);

    List<SpaceSearch> getSearch(User user, int limit, int page, List<Long> tags_id, String search);

    void createSpace(User owner, String name, List<Long> members, List<String> tags, boolean isPublic);

    void updateSpace(User user, Long spaceId, String name, Map<String, ArrayList<Long>> members, Map<String, ArrayList<String>> tags, boolean isPublic);

    Space getSpace(User user, Long spaceId);

    void deleteSpaceById(User user, Long spaceId);

    // TODO: change return type to Long
    void createBlock(User user, Long spaceId, String name);

    void updateBlock(User user, Long spaceId, Long blockId, String name);

    void deleteBlock(User user, Long spaceId, Long blockId);

    Long createCard(User user, Long spaceId, Long blockId, String name, String description);

    void updateCard(User user, Long spaceId, Long blockId, Long cardId, String name, String description);

    void deleteCard(User user, Long spaceId, Long blockId, Long cardId);
}
