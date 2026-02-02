package com.nathan.lock_in.auth;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String email;
    private String password;
}
