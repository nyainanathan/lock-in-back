package com.nathan.lock_in.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class Users {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Instant createdAt;
    private String passwordHash;
}
