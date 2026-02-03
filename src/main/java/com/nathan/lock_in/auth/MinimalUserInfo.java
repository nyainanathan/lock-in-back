package com.nathan.lock_in.auth;

import com.nathan.lock_in.user.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class MinimalUserInfo {
    private String id;
    private String firstName;
    private String lastName;
    private String email;

    public MinimalUserInfo(Users user) {
        setLastName(user.getLastName());
        setFirstName(user.getFirstName());
        setId(user.getId());
        setEmail(user.getEmail());
    }
}
