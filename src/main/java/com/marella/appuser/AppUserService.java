package com.marella.appuser;

import com.marella.appauthandregiser.AuthenticationRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AppUserService /*implements UserDetailsService*/{
    AppUserRepository appUserRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public String singIn(String email, String password) {
        Optional<AppUser> user = appUserRepository.findByEmail(email);
        if(user.isEmpty()){
            return "user not exist";
        }
        if(!bCryptPasswordEncoder.matches(password, user.get().getPassword())){
            return "password is incorrect";
        }
        return user.get().toString();
    }

    public String signUp(String email, String username, String password) {
        if(appUserRepository.findByEmail(email).isPresent()){
            return "email already exist";
        }
        if(appUserRepository.findByUsername(username).isPresent()){
            return "username already exist";
        }
//        Метод не закончен, не тестировать
        return "";
    }

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return null;
//    }
}
