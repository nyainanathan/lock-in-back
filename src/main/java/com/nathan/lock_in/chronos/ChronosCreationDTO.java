package com.nathan.lock_in.chronos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ChronosCreationDTO {
    private String userId;
    private Double duration;
    private String title;
    private DurationUnit unit;
    private String projectId;
}
