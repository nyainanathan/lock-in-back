package com.nathan.lock_in.chronos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@Getter
@Setter
public class ChronosCreationDTO {
    private String userId;
    private Double duration;
    private String title;
    private DurationUnit unit;
    private String projectId;
    private Instant createdAt;
    private Instant stoppedAt;
}
