package com.nathan.lock_in.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class MinimalUserInfo {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
}
