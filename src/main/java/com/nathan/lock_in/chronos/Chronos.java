package com.nathan.lock_in.chronos;

import com.nathan.lock_in.auth.MinimalUserInfo;
import com.nathan.lock_in.projects.Projects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Chronos {
    private String id;
    private MinimalUserInfo user;
    private Double duration;
    private String title;
    private Instant createdAt;
    private DurationUnit unit;
    private Projects project;
    private Instant stoppedAt;
}
