package com.nathan.lock_in.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreationDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
}
