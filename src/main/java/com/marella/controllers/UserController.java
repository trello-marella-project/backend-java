package com.marella.controllers;

import com.marella.models.User;
import com.marella.payload.request.EmailRequest;
import com.marella.payload.request.MessageRequest;
import com.marella.payload.request.UploadFileRequest;
import com.marella.payload.response.UserGetAdminResponse;
import com.marella.payload.response.UserGetResponse;
import com.marella.security.jwt.JwtUtils;
import com.marella.services.UserService;
import com.marella.utils.FileUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private JwtUtils jwtUtils;

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

    @PostMapping("/{user_id}")
    public ResponseEntity<?> createReport(@PathVariable Long user_id,
                                          @RequestBody MessageRequest messageRequest,
                                          @RequestHeader(name = "Authorization") String authorization){
        try{
            userService.createReport(getUser(authorization), user_id, messageRequest.getMessage());
            return ResponseEntity.status(CREATED)
                    .contentType(APPLICATION_JSON)
                    .body("{\"status\":\"success\"}");
        } catch (IllegalArgumentException e){
            logger.error(e.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(APPLICATION_JSON)
                    .body(String.format("{\"status\":\"Error: %s\"}", e.getMessage()));
        }
    }

    @PostMapping(value = "/photo", consumes = {MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadPhoto(@ModelAttribute UploadFileRequest uploadFileRequest,
//    public ResponseEntity<?> uploadPhoto(@ModelAttribute @RequestPart MultipartFile document,
                                         @RequestHeader(name = "Authorization") String authorization){
        logger.info("file received");
        String localPath="D:/Семен/УЧЕБА/Курс 3/Marella/src/main/resources/static";
        String filename = getUser(authorization).getUsername();
        try {
            FileUtils.upload(uploadFileRequest.getFile(), localPath, filename + ".jpeg");
            return ResponseEntity.ok()
                    .contentType(APPLICATION_JSON)
                    .body("{\"status\":\"success\"}");
        }catch (IllegalStateException | IOException e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
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
