package com.marella.controllers;

import com.marella.models.Space;
import com.marella.models.User;
import com.marella.payload.request.BlockRequest;
import com.marella.payload.request.CardRequest;
import com.marella.payload.response.BlockResponse;
import com.marella.payload.response.CardResponse;
import com.marella.repositories.UserRepository;
import com.marella.security.jwt.JwtUtils;
import com.marella.services.SpaceService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
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
                    .body(String.format("{\"status\":\"Error: %s\"}", e.getMessage()));
        }
        return ResponseEntity.ok()
                .contentType(APPLICATION_JSON)
                .body(space.toString());
    }

    @PostMapping("/{workspace_id}/block")
    public ResponseEntity<?> createBlock(@PathVariable Long workspace_id,
                                         @RequestBody BlockRequest blockRequest,
                                         @RequestHeader(name = "Authorization") String authorization){
        User user = getUser(authorization);
        try{
            spaceService.createBlock(user, workspace_id, blockRequest.getName());
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(APPLICATION_JSON)
                    .body(String.format("{\"status\":\"Error: %s\"}", e.getMessage()));
        }
        return ResponseEntity.status(CREATED)
                .contentType(APPLICATION_JSON)
                // TODO: change response
                .body("{\"status\":\"success\"}");
    }

    @PutMapping("/{workspace_id}/block/{block_id}")
    public ResponseEntity<?> updateBlock(@PathVariable Long workspace_id,
                                         @PathVariable Long block_id,
                                         @RequestBody BlockRequest blockRequest,
                                         @RequestHeader(name = "Authorization") String authorization){
        User user = getUser(authorization);
        try{
            spaceService.updateBlock(user, workspace_id, block_id, blockRequest.getName());
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(APPLICATION_JSON)
                    .body(String.format("{\"status\":\"Error: %s\"}", e.getMessage()));
        }
        return ResponseEntity.ok()
                .contentType(APPLICATION_JSON)
                .body(new BlockResponse(block_id, blockRequest.getName(), workspace_id));
    }

    @DeleteMapping("/{workspace_id}/block/{block_id}")
    public ResponseEntity<?> deleteBlock(@PathVariable Long workspace_id,
                                         @PathVariable Long block_id,
                                         @RequestHeader(name = "Authorization") String authorization){
        User user = getUser(authorization);
        try{
            spaceService.deleteBlock(user, workspace_id, block_id);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(APPLICATION_JSON)
                    .body(String.format("{\"status\":\"Error: %s\"}", e.getMessage()));
        }
        return ResponseEntity.ok()
                .contentType(APPLICATION_JSON)
                .body("{\"status\":\"success\"}");
    }

    @PostMapping("/{workspace_id}/block/{block_id}/card")
    public ResponseEntity<?> createCard(@PathVariable Long workspace_id,
                                        @PathVariable Long block_id,
                                        @RequestBody CardRequest cardRequest,
                                        @RequestHeader(name = "Authorization") String authorization){
        User user = getUser(authorization);
        Long cardId;
        try{
            cardId = spaceService.createCard(user, workspace_id, block_id, cardRequest.getName(), cardRequest.getDescription());
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(APPLICATION_JSON)
                    .body(String.format("{\"status\":\"Error: %s\"}", e.getMessage()));
        }
        return ResponseEntity.status(CREATED)
                .contentType(APPLICATION_JSON)
                .body(new CardResponse(cardId, block_id, cardRequest.getName()).toString());
    }

    @PutMapping("/{workspace_id}/block/{block_id}/card/{card_id}")
    public ResponseEntity<?> updateCard(@PathVariable Long workspace_id,
                                        @PathVariable Long block_id,
                                        @PathVariable Long card_id,
                                        @RequestBody CardRequest cardRequest,
                                        @RequestHeader(name = "Authorization") String authorization){
        User user = getUser(authorization);
        try{
            spaceService.updateCard(user, workspace_id, block_id, card_id,
                    cardRequest.getName(), cardRequest.getDescription());
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(APPLICATION_JSON)
                    .body(String.format("{\"status\":\"Error: %s\"}", e.getMessage()));
        }
        return ResponseEntity.ok()
                .contentType(APPLICATION_JSON)
                .body(new CardResponse(card_id, block_id, cardRequest.getName()).toString());
    }

    @DeleteMapping("/{workspace_id}/block/{block_id}/card/{card_id}")
    public ResponseEntity<?> deleteCard(@PathVariable Long workspace_id,
                                        @PathVariable Long block_id,
                                        @PathVariable Long card_id,
                                        @RequestBody CardRequest cardRequest,
                                        @RequestHeader(name = "Authorization") String authorization){
        User user = getUser(authorization);
        try{
            spaceService.deleteCard(user, workspace_id, block_id, card_id,
                    cardRequest.getName(), cardRequest.getDescription());
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(APPLICATION_JSON)
                    .body(String.format("{\"status\":\"Error: %s\"}", e.getMessage()));
        }
        return ResponseEntity.ok()
                .contentType(APPLICATION_JSON)
                .body("{\"status\":\"success\"}");
    }

    private User getUser(String authorization) {
        String token = authorization.substring(7);
        String username = jwtUtils.getUserNameFromJwtToken(token);
        return userRepository.findByUsername(username).get();
    }
}
