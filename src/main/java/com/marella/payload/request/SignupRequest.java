package com.marella.payload.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class SignupRequest {
    private String username;
    private String email;
    private String password;
}