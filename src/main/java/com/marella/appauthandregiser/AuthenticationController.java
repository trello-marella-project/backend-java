package com.marella.appauthandregiser;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "auth")
@AllArgsConstructor
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping(path = "login")
    public String authentication(@RequestBody AuthenticationRequest authenticationRequest){
        return authenticationService.authentication(authenticationRequest);
    }

    @PostMapping(path = "register")
    public String register(@RequestBody RegistrationRequest registrationRequest){
        return authenticationService.registration(registrationRequest);
    }
}
