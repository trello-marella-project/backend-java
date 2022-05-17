package com.marella.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;

@Setter
@Getter
public class LoginRequest {
    @Email
    private String email;
    private String password;
}