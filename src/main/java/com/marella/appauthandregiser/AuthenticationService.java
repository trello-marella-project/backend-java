package com.marella.appauthandregiser;

import com.marella.appuser.AppUserRepository;
import com.marella.appuser.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationService {
    AppUserRepository appUserRepository;
    AppUserService appUserService;

    public String authentication(AuthenticationRequest request) {
        String response = appUserService.singIn(request.getEmail(), request.getPassword());
        // TODO : add token
        return response;
    }

    public String registration(RegistrationRequest request) {
        String response = appUserService.signUp(request.getEmail(), request.getUsername(), request.getPassword());
        //        Метод не закончен, не тестировать
        //
        return "";
    }
}
