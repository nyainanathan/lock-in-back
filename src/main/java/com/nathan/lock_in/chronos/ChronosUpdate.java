package com.nathan.lock_in.chronos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChronosUpdate {
    private String id;
    private Double duration;
    private String title;
    private DurationUnit unit;
}
