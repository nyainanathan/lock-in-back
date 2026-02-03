package com.nathan.lock_in.projects;

import com.nathan.lock_in.auth.MinimalUserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@Getter
@Setter
public class Projects {
    private String id;
    private MinimalUserInfo user;
    private String title;
    private String description;
    private Instant createdAt;

    public Projects() {

    }
}
