package com.marella.services;

import com.marella.dbrequests.OffsetBasedPageRequest;
import com.marella.models.Permission;
import com.marella.models.Space;
import com.marella.models.Tag;
import com.marella.models.User;
import com.marella.payload.SpaceSearch;
import com.marella.payload.response.SpaceResponse;
import com.marella.repositories.PermissionRepository;
import com.marella.repositories.SpaceRepository;
import com.marella.repositories.TagRepository;
import com.marella.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SpaceServiceImpl implements SpaceService{
    private SpaceRepository spaceRepository;
    private UserRepository userRepository;
    private TagRepository tagRepository;
    private PermissionRepository permissionRepository;

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
        Pageable pageable = new OffsetBasedPageRequest(limit, limit * page);
        return spaceRepository.findRecentByUser(user.getId(), pageable);
    }

    @Override
    public List<SpaceSearch> getSearch(User user, int limit, int page, List<Long> tags_id, String search) {
//        TODO: customize searching process
//        TODO: customize request to DB
        Pageable pageable = new OffsetBasedPageRequest(limit, limit * page);
        return spaceRepository.findSpaces(user.getId(), tags_id, search, pageable);
    }

    @Override
    public void createSpace(User owner, String name, List<Long> members, List<String> tags, boolean isPublic) throws IllegalArgumentException {
        List<User> users = new ArrayList<>();
        for(Long id : members) {
            Optional<User> user = userRepository.findById(id);
            if (user.isEmpty() || !user.get().getEnabled())
                throw new IllegalArgumentException(String.format("user with id: %d does not exist", id));
            users.add(user.get());
        }
        Space space = new Space(name, isPublic);
        space.setUser(owner);

        for(User user : users)
            space.addPermission(new Permission(user, space));
        for(String tagName : tags)
            space.addTag(new Tag(tagName));
//            tagRepository.findByName(tagName).ifPresentOrElse(space::addTag, () -> space.addTag(new Tag(tagName)));

        spaceRepository.save(space);
    }
}
