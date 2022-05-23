package com.marella.services;

import com.marella.dbrequests.OffsetBasedPageRequest;
import com.marella.models.*;
import com.marella.payload.SpaceSearch;
import com.marella.payload.response.SpaceResponse;
import com.marella.repositories.PermissionRepository;
import com.marella.repositories.SpaceRepository;
import com.marella.repositories.TagRepository;
import com.marella.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@AllArgsConstructor
public class SpaceServiceImpl implements SpaceService{
    private static final Logger logger = LoggerFactory.getLogger(SpaceServiceImpl.class);

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
    @Transactional
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

        space.addEntrance(new Entrance(owner, space, new GregorianCalendar()));
        spaceRepository.save(space);
    }

    @Override
    public Space getSpace(User user, Long spaceId) {
        Space space = spaceRepository.findById(spaceId).orElseThrow(
                () -> new IllegalArgumentException(String.format("space with id: %d does not exist", spaceId))
        );
        if(space.isPublic()) return space;

        Long userId = user.getId();
        if(space.getUser().getId().equals(userId)) return space;
        for(Permission permission : space.getPermissions()){
            if(permission.getUser().getId().equals(userId))
                return space;
        }
        throw new IllegalArgumentException(String.format("forbidden to get space with id: %d", spaceId));
    }

    @Override
    @Transactional
    public void updateSpace(User owner, Long spaceId, String name, Map<String, ArrayList<Long>> members,
                            Map<String, ArrayList<String>> tags, boolean isPublic) throws IllegalArgumentException{
        Space space = spaceRepository.findById(spaceId).orElseThrow(
                () -> new IllegalArgumentException(String.format("space with id: %d does not exist", spaceId))
        );
        if(space.getUser() != owner)
            throw new IllegalArgumentException(String.format("forbidden to update space with id: %d", spaceId));

        for(Long userId : members.get("added")){
            User user = userRepository.findById(userId).orElseThrow(
                    () -> new IllegalArgumentException(String.format("user with id: %d does not exist", userId))
            );
            space.addPermission(new Permission(user, space));
        }
        for(Long userId : members.get("deleted")){
            User user = userRepository.findById(userId).orElseThrow(
                    () -> new IllegalArgumentException(String.format("user with id: %d does not exist", userId))
            );
            Optional<Permission> permission = permissionRepository.findByUserAndSpace(user, space);
            permission.ifPresentOrElse(space::removePermission,
                    () -> logger.error(String.format("Permission for user with id %d and for space with id %d not exist",
                            userId, spaceId)));
            permission.ifPresent(value -> logger.info(value.getId().toString()));
        }
        for(String tagName : tags.get("added")){
            space.addTag(new Tag(tagName));
        }
        for(String tagName : tags.get("deleted")){
            Optional<Tag> tag = tagRepository.findByNameAndSpace(tagName, space);
            tag.ifPresent(space::removeTag);
        }
        space.setName(name);
        space.setPublic(isPublic);
    }


    @Override
    public void deleteSpaceById(User user, Long spaceId) {
        Space space = spaceRepository.findById(spaceId).orElseThrow(
                () -> new IllegalArgumentException(String.format("space with id: %d does not exist", spaceId))
        );
        if(space.getUser() != user)
            throw new IllegalArgumentException(String.format("forbidden to delete space with id: %d", spaceId));
        spaceRepository.delete(space);
    }
}
