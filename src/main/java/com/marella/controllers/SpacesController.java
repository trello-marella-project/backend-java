package com.marella.controllers;

import com.marella.models.User;
import com.marella.repositories.TagRepository;
import com.marella.repositories.UserRepository;
import com.marella.security.jwt.JwtUtils;
import com.marella.services.SpaceService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<?> userSpaces(@RequestParam("limit") Long limit,
                                        @RequestParam("page") Long page,
                                        @RequestHeader(name = "Authorization") String authorization){
        User user = getUser(authorization);
        return ResponseEntity.ok(spaceService.getUserSpacesByLimitAndPage(user, limit, page));
    }

    @GetMapping("/permitted")
    public ResponseEntity<?> userPermittedSpaces(@RequestParam("limit") Long limit,
                                                 @RequestParam("page") Long page,
                                                 @RequestHeader(name = "Authorization") String authorization){
        User user = getUser(authorization);
        return ResponseEntity.ok(spaceService.getUserPermittedSpacesByLimitAndPage(user, limit, page));
    }

    @GetMapping("/recent")
    public ResponseEntity<?> userRecentSpaces(@RequestParam("limit") Long limit,
                                              @RequestParam("page") Long page,
                                              @RequestHeader(name = "Authorization") String authorization){
        User user = getUser(authorization);
        return ResponseEntity.ok(spaceService.getUserRecentSpacesByLimitAndPage(user, limit, page));
    }

    @GetMapping("/")
    public ResponseEntity<?> userRecentSpaces(@RequestParam("limit") Long limit,
                                              @RequestParam("page") Long page,
                                              @RequestParam("tags_id") List<Long> tags_id,
                                              @RequestParam("search") String search,
                                              @RequestHeader(name = "Authorization") String authorization){
        User user = getUser(authorization);
        return ResponseEntity.ok(spaceService.getSearch(user, limit, page, tags_id, search));
    }

    @GetMapping("/tags")
    public ResponseEntity<?> userRecentSpaces(){
        return ResponseEntity.ok(tagRepository.findAll());
    }

    private User getUser(String authorization) {
        String token = authorization.substring(7);
        String username = jwtUtils.getUserNameFromJwtToken(token);
        return userRepository.findByUsername(username).get();
    }


}
