package com.marella.controllers;

import com.marella.models.User;
import com.marella.payload.request.EmailRequest;
import com.marella.payload.response.UserGetAdminResponse;
import com.marella.payload.response.UserGetResponse;
import com.marella.repositories.UserRepository;
import com.marella.security.jwt.JwtUtils;
import com.marella.services.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private JwtUtils jwtUtils;

    private UserRepository userRepository;

    private UserService userService;

    @PostMapping("/email")
    public ResponseEntity<?> getUser(@Valid @RequestBody EmailRequest emailRequest){
        try{
            User user = userService.findUserByEmail(emailRequest.getEmail());
            return ResponseEntity.ok()
                    .contentType(APPLICATION_JSON)
                    .body(new UserGetResponse(user.getId(), user.getUsername(), user.getEmail()).toString());
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(APPLICATION_JSON)
                    .body(String.format("{\"status\":\"Error: %s\"}", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(){
        try{
            List<User> allUsers = userService.findAllUsers();
            return ResponseEntity.ok()
                    .contentType(APPLICATION_JSON)
                    .body(new UserGetAdminResponse(allUsers).toString());
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(APPLICATION_JSON)
                    .body(String.format("{\"status\":\"Error: %s\"}", e.getMessage()));
        }
    }

    private User getUser(String authorization) {
        String token = authorization.substring(7);
        String username = jwtUtils.getUserNameFromJwtToken(token);
        return userService.findUserByUsername(username);
    }
}
