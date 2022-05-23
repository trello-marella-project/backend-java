package com.marella.controllers;

import com.marella.models.Space;
import com.marella.models.User;
import com.marella.payload.request.SpaceCreationRequest;
import com.marella.payload.request.SpaceUpdateRequest;
import com.marella.payload.response.GetSpaceResponse;
import com.marella.repositories.TagRepository;
import com.marella.repositories.UserRepository;
import com.marella.security.jwt.JwtUtils;
import com.marella.services.SpaceService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@RestController
@RequestMapping("/api/spaces")
@AllArgsConstructor
public class SpacesController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private SpaceService spaceService;
    private JwtUtils jwtUtils;
    private UserRepository userRepository;
    private TagRepository tagRepository;

    @GetMapping("/yours")
    public ResponseEntity<?> userSpaces(@RequestParam("limit") int limit,
                                        @RequestParam("page") int page,
                                        @RequestHeader(name = "Authorization") String authorization,
                                        HttpServletResponse response) {
        User user = getUser(authorization);
        logger.info("username " + user.getUsername());
        return ResponseEntity.ok()
                .contentType(APPLICATION_JSON)
                .body(ResponseRender(spaceService.getUserSpacesByLimitAndPage(user, limit, page), "spaces"));
    }

    @GetMapping("/permitted")
    public ResponseEntity<?> userPermittedSpaces(@RequestParam("limit") int limit,
                                                 @RequestParam("page") int page,
                                                 @RequestHeader(name = "Authorization") String authorization) {
        User user = getUser(authorization);
        logger.info("username " + user.getUsername());
        return ResponseEntity.ok()
                .contentType(APPLICATION_JSON)
                .body(ResponseRender(spaceService.getUserPermittedSpacesByLimitAndPage(user, limit, page), "spaces"));
    }

    @GetMapping("/recent")
    public ResponseEntity<?> userRecentSpaces(@RequestParam("limit") int limit,
                                              @RequestParam("page") int page,
                                              @RequestHeader(name = "Authorization") String authorization) {
        User user = getUser(authorization);
        logger.info("username " + user.getUsername());
        return ResponseEntity.ok()
                .contentType(APPLICATION_JSON)
                .body(ResponseRender(spaceService.getUserRecentSpacesByLimitAndPage(user, limit, page), "spaces"));
    }

    // TODO: correct parameters of request
    @GetMapping("/")
    public ResponseEntity<?> userFindSpaces(@RequestParam("limit") int limit,
                                            @RequestParam("page") int page,
                                            @RequestParam("tags_id") List<Long> tags_id,
                                            @RequestParam("search") String search,
                                            @RequestHeader(name = "Authorization") String authorization) {
        User user = getUser(authorization);
        return ResponseEntity.ok()
                .contentType(APPLICATION_JSON)
                .body(ResponseRender(spaceService.getSearch(user, limit, page, tags_id, search), "spaces"));
    }

    @PostMapping()
    public ResponseEntity<?> createSpace(@RequestBody SpaceCreationRequest spaceCreationRequest,
                                         @RequestHeader(name = "Authorization") String authorization) {
        User user = getUser(authorization);
        logger.info(user.getUsername());
        logger.info(spaceCreationRequest.getName());
        logger.info(spaceCreationRequest.getMembers().toString());
        logger.info(spaceCreationRequest.getTags().toString());
        logger.info(String.valueOf(spaceCreationRequest.is_public()));
        try {
            spaceService.createSpace(user,
                    spaceCreationRequest.getName(),
                    spaceCreationRequest.getMembers(),
                    spaceCreationRequest.getTags(),
                    spaceCreationRequest.is_public());
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).contentType(APPLICATION_JSON).body("success");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSpace(@PathVariable("id") Long spaceId,
                                      @RequestHeader(name = "Authorization") String authorization){
        User user = getUser(authorization);
        Space space;
        try {
            space = spaceService.getSpace(user, spaceId);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
        return ResponseEntity.ok().contentType(APPLICATION_JSON).body(new GetSpaceResponse(space).toString());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSpace(@RequestBody SpaceUpdateRequest spaceUpdateRequest,
                                         @PathVariable("id") Long spaceId,
                                         @RequestHeader(name = "Authorization") String authorization){
        User user = getUser(authorization);
        logger.info(String.valueOf(spaceUpdateRequest.is_public()));
        try {
            spaceService.updateSpace(user,
                    spaceId,
                    spaceUpdateRequest.getName(),
                    spaceUpdateRequest.getMembers(),
                    spaceUpdateRequest.getTags(),
                    spaceUpdateRequest.is_public());
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
        return ResponseEntity.ok().contentType(APPLICATION_JSON).body("success");
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<?> deleteSpace(@PathVariable("id") Long spaceId,
                                         @RequestHeader(name = "Authorization") String authorization){
        User user = getUser(authorization);
        try {
            spaceService.deleteSpaceById(user, spaceId);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
        return ResponseEntity.ok().contentType(APPLICATION_JSON).body("success");
    }

    @GetMapping("/tags")
    public ResponseEntity<?> spacesTags() {
        return ResponseEntity.ok()
                .contentType(APPLICATION_JSON)
                .body(ResponseRender(tagRepository.findAll(), "tags"));
    }

    private User getUser(String authorization) {
        String token = authorization.substring(7);
        String username = jwtUtils.getUserNameFromJwtToken(token);
        return userRepository.findByUsername(username).get();
    }

    private String ResponseRender(List<?> spaceResponses, String type) {
        logger.info(spaceResponses.toString());
        if (type.isEmpty()) return null;
        if (spaceResponses.isEmpty()) return String.format("{\"%s\":[]}", type);
        StringBuilder response = new StringBuilder();
        String prefix = String.format("{\"%s\":[", type);
        for (Object spaceResponse : spaceResponses) {
            response.append(prefix);
            prefix = ",";
            response.append(spaceResponse.toString());
        }
        response.append("]}");
        return response.toString();
    }
}
