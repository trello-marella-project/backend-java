package com.marella.services;

import com.marella.dbrequests.OffsetBasedPageRequest;
import com.marella.models.*;
import com.marella.payload.SpaceSearch;
import com.marella.payload.response.SpaceResponse;
import com.marella.repositories.*;
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
    private EntranceRepository entranceRepository;
    private BlockRepository blockRepository;

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
        if(isPermitted(user, space)) return space;
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
    public void deleteSpaceById(User user, Long spaceId) throws IllegalArgumentException{
        Space space = spaceRepository.findById(spaceId).orElseThrow(
                () -> new IllegalArgumentException(String.format("space with id: %d does not exist", spaceId))
        );
        if(space.getUser() != user)
            throw new IllegalArgumentException(String.format("forbidden to delete space with id: %d", spaceId));
        spaceRepository.delete(space);
    }

    @Override
    @Transactional
    public void createBlock(User user, Long spaceId, String name) {
        Space space = spaceRepository.findById(spaceId).orElseThrow(
                () -> new IllegalArgumentException(String.format("space with id: %d does not exist", spaceId))
        );
        if(!isPermitted(user, space))
            throw new IllegalArgumentException(String.format("forbidden to change workspace with id: %d", spaceId));

        space.addBlock(new Block(name));
        spaceRepository.save(space);
    }

    @Override
    @Transactional
    public void updateBlock(User user, Long spaceId, Long blockId, String name) {
        Space space = spaceRepository.findById(spaceId).orElseThrow(
                () -> new IllegalArgumentException(String.format("space with id: %d does not exist", spaceId))
        );
        if(!isPermitted(user, space))
            throw new IllegalArgumentException(String.format("forbidden to change workspace with id: %d", spaceId));
        Block block = blockRepository.findById(blockId).orElseThrow(
                () -> new IllegalArgumentException(String.format("block with id: %d does not exist", blockId))
        );
        List<Block> blocks = space.getBlocks();
        int index = blocks.indexOf(block);
        if(index == -1) throw new IllegalArgumentException(String.format("workspace do not contains block with id: %d ", blockId));

        blocks.get(index).setName(name);
        updateEntranceTime(user, space);
    }

    @Override
    @Transactional
    public void deleteBlock(User user, Long spaceId, Long blockId) {
        Space space = spaceRepository.findById(spaceId).orElseThrow(
                () -> new IllegalArgumentException(String.format("space with id: %d does not exist", spaceId))
        );
        if(!isPermitted(user, space))
            throw new IllegalArgumentException(String.format("forbidden to change workspace with id: %d", spaceId));
        Block block = blockRepository.findById(blockId).orElseThrow(
                () -> new IllegalArgumentException(String.format("block with id: %d does not exist", blockId))
        );
        List<Block> blocks = space.getBlocks();
        int index = blocks.indexOf(block);
        if(index == -1) throw new IllegalArgumentException(String.format("workspace do not contains block with id: %d ", blockId));

        space.removeBlock(block);
        updateEntranceTime(user, space);
    }

    @Override
    @Transactional
    public Long createCard(User user, Long spaceId, Long blockId, String name, String description) {
        Space space = spaceRepository.findById(spaceId).orElseThrow(
                () -> new IllegalArgumentException(String.format("space with id: %d does not exist", spaceId))
        );
        if(!isPermitted(user, space))
            throw new IllegalArgumentException(String.format("forbidden to change workspace with id: %d", spaceId));
        Block block = blockRepository.findById(blockId).orElseThrow(
                () -> new IllegalArgumentException(String.format("block with id: %d does not exist", blockId))
        );
        List<Block> blocks = space.getBlocks();
        int index = blocks.indexOf(block);
        if(index == -1) throw new IllegalArgumentException(String.format("workspace do not contains block with id: %d ", blockId));

        Card card = new Card(name, description);
        block.addCard(card);
        updateEntranceTime(user, space);
        return card.getId();
    }

    private void updateEntranceTime(User user, Space space) {
        Optional<Entrance> entrance = entranceRepository.findByUserAndSpace(user, space);
        if(entrance.isPresent()) {
            entrance.get().setDate(new GregorianCalendar());
            entranceRepository.save(entrance.get());
        }
        else space.addEntrance(new Entrance(user, space, new GregorianCalendar()));
        spaceRepository.save(space);
    }

    private boolean isPermitted(User user, Space space){
        if(space.isPublic()) return true;
        Long userId = user.getId();
        if(space.getUser().getId().equals(userId)) return true;
        for(Permission permission : space.getPermissions()){
            if(permission.getUser().getId().equals(userId))
                return true;
        }
        return false;
    }
}
