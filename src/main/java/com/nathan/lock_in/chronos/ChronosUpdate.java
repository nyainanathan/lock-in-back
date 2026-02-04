package com.nathan.lock_in.chronos;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class ChronosUpdate {
    private String id;
    private Double duration;
    private String title;
    private DurationUnit unit;
    private Instant createdAt;
    private Instant stoppedAt;
}
