package com.marella.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import java.util.Set;

@Setter
@Getter
public class SignupRequest {
    private String username;
    @Email
    private String email;
    private String password;
}