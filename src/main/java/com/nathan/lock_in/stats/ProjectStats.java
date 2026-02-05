package com.nathan.lock_in.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProjectStats {
    private String id;
    private String name;
    private double focusedMinutes;
}
