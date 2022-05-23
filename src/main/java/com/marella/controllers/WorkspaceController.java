package com.marella.controllers;

import com.marella.models.Space;
import com.marella.models.User;
import com.marella.repositories.UserRepository;
import com.marella.security.jwt.JwtUtils;
import com.marella.services.SpaceService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Controller
@RequestMapping("/api/workspace")
@AllArgsConstructor
public class WorkspaceController {
    private static final Logger logger = LoggerFactory.getLogger(WorkspaceController.class);

    private SpaceService spaceService;
    private JwtUtils jwtUtils;
    private UserRepository userRepository;

    @GetMapping("/{workspace_id}")
    public ResponseEntity<?> getWorkspaceById(@PathVariable Long workspace_id,
                                              @RequestHeader(name = "Authorization") String authorization){
        User user = getUser(authorization);
        Space space;
        try{
            space = spaceService.getSpace(user, workspace_id);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(APPLICATION_JSON)
                    .body(String.format("{Error: %s}", e.getMessage()));
        }
        return ResponseEntity.ok()
                .contentType(APPLICATION_JSON)
                .body(space.toString());
    }

    private User getUser(String authorization) {
        String token = authorization.substring(7);
        String username = jwtUtils.getUserNameFromJwtToken(token);
        return userRepository.findByUsername(username).get();
    }
}
