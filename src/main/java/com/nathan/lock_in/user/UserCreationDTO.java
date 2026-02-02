package com.nathan.lock_in.user;

import lombok.Getter;

@Getter
public class UserCreationDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
}
