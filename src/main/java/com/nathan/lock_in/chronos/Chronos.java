package com.nathan.lock_in.chronos;

import com.nathan.lock_in.user.Users;
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
    private Users user;
    private Double duration;
    private String title;
    private Instant createdAt;
    private DurationUnit unit;
}
