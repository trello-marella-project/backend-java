package com.marella.controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

import com.marella.email.EmailService;
import com.marella.exception.TokenRefreshException;
import com.marella.models.*;
import com.marella.payload.request.TokenRefreshRequest;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        // authentication
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // is user enabled (set as filter)
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        String username = userPrincipal.getUsername();
        if(!userRepository.findByUsername(username).get().getEnabled()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Warning: Email is not activated");
        }

        // generating jwt
        String jwt = jwtUtils.generateTokenFromUsername(username);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        // constructing response
        Cookie cookie = new Cookie("refresh", refreshToken.getToken());
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return ResponseEntity.ok(new JwtResponse(jwt,
                refreshToken.getToken(),
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/refreshtoken")
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
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());

                    Cookie newCookie = new Cookie("refresh", refreshToken.getToken());
                    newCookie.setHttpOnly(true);
                    response.addCookie(newCookie);
                    return ResponseEntity.ok(new TokenRefreshResponse(token, refreshToken.getToken()));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
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
        userRepository.save(user);

        ActivationToken activationToken = new ActivationToken(UUID.randomUUID().toString(), user);
        activationTokenRepository.save(activationToken);
        emailService.send(user.getEmail(), "http://localhost:8080/api/auth/activate/" + activationToken.getToken());
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