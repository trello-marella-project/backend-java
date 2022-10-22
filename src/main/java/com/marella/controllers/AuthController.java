package com.marella.controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

import com.marella.email.EmailService;
import com.marella.exception.TokenRefreshException;
import com.marella.models.*;
import com.marella.payload.response.TokenRefreshResponse;
import com.marella.repositories.ActivationTokenRepository;
import com.marella.security.services.RefreshTokenService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.marella.payload.request.LoginRequest;
import com.marella.payload.request.SignupRequest;
import com.marella.payload.response.JwtResponse;
import com.marella.payload.response.MessageResponse;
import com.marella.repositories.RoleRepository;
import com.marella.repositories.UserRepository;
import com.marella.security.jwt.JwtUtils;
import com.marella.security.services.UserDetailsImpl;
import org.springframework.web.client.HttpServerErrorException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    AuthenticationManager authenticationManager;

    UserRepository userRepository;

    RoleRepository roleRepository;

    ActivationTokenRepository activationTokenRepository;

    PasswordEncoder encoder;

    JwtUtils jwtUtils;

    RefreshTokenService refreshTokenService;

    EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        // authentication
        logger.info("get username by email");
        String email = loginRequest.getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("user not found with email: " + email));

        logger.info("authenticate");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // is user enabled TODO: set as filter
        logger.info("get details");
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//        String username = userDetails.getUsername();
        if(!user.getEnabled()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(APPLICATION_JSON)
                    .body("{\"status\":\"warning: email is not activated\"}");
        }
        if(user.getBlocked()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(APPLICATION_JSON)
                    .body("{\"status\":\"warning: user is blocked\"}");
        }

        logger.info("create jwt");
//        String jwt = jwtUtils.generateTokenFromUsername(username);
        String jwt = jwtUtils.generateTokenFromUserId(user.getId());
        logger.info("get roles");
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        logger.info("create refresh");
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        logger.info("set cookies");
        // constructing response
        Cookie cookie = new Cookie("refresh", refreshToken.getToken());
//        cookie.setHttpOnly(true);
//        cookie.setSecure(true);
        response.addCookie(cookie);
        response.setHeader("Set-Cookie", "key=value; Secure; Path:\"/\"; SameSite=None");
        logger.info("return response");
        return ResponseEntity.ok()
                .contentType(APPLICATION_JSON)
                .body(new JwtResponse(jwt,
                        refreshToken.getToken(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        roles,
                        userDetails.isEnabled()).toString());
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshtoken(@CookieValue(value = "refresh") Cookie cookie, HttpServletResponse response) {
        if(cookie == null){
            throw new HttpServerErrorException(HttpStatus.BAD_REQUEST, "Error: cookie for refreshing not found");
        }
        logger.info("request cookie is " + cookie.getValue());
        String requestRefreshToken = cookie.getValue();
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::deleteToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
//                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    String token = jwtUtils.generateTokenFromUserId(user.getId());
                    refreshTokenService.deleteExpiredTokensByUser(user);

                    Cookie newCookie = new Cookie("refresh", refreshToken.getToken());
//                    newCookie.setHttpOnly(true);
//                    newCookie.setSecure(true);
                    response.addCookie(newCookie);
                    response.setHeader("Set-Cookie", "key=value; Secure; Path:\"/\"; SameSite=None");
                    return ResponseEntity.ok(new TokenRefreshResponse(token, refreshToken.getToken()));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        Optional<User> dbuser = userRepository.findByUsername(signUpRequest.getUsername());
        if (dbuser.isPresent()) {
            if(!dbuser.get().getEnabled()){
                Optional<ActivationToken> token = activationTokenRepository.findByUser(dbuser.get());
                if(token.isPresent()) {
                    emailService.send(dbuser.get().getEmail(),
                            "https://marella-api.herokuapp.com/api/auth/activate/" + token.get().getToken());
                    return ResponseEntity.ok().contentType(APPLICATION_JSON).body("{\"status\":\"success\"}");
                }
            }
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }
        dbuser = userRepository.findByEmail(signUpRequest.getEmail());
        if (dbuser.isPresent()) {
            if(!dbuser.get().getEnabled()){
                Optional<ActivationToken> token = activationTokenRepository.findByUser(dbuser.get());
                if(token.isPresent()) {
                    emailService.send(dbuser.get().getEmail(),
                            "https://marella-api.herokuapp.com/api/auth/activate/" + token.get().getToken());
                    return ResponseEntity.ok().contentType(APPLICATION_JSON).body("{\"status\":\"success\"}");
                }
            }
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }
        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        user.setRoles(roles);

        ActivationToken activationToken = new ActivationToken(UUID.randomUUID().toString(), user);
        emailService.send(user.getEmail(), "https://marella-api.herokuapp.com/api/auth/activate/" + activationToken.getToken());

        userRepository.save(user);
        activationTokenRepository.save(activationToken);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @GetMapping("/activate/{token}")
    public ResponseEntity<?> activation(@PathVariable("token") String token) throws URISyntaxException {
        ActivationToken activationToken = activationTokenRepository.getByToken(token)
                .orElseThrow(() -> new RuntimeException("Error: token is incorrect"));
        activationTokenRepository.delete(activationToken);
        logger.info("activation token deleted");

        User user = activationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        logger.info("user set as enabled");

        logger.info("redirect");
        URI externalUri = new URI("https://vk.com/s_larkin");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(externalUri);

        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }
}