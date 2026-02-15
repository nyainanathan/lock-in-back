package com.nathan.lock_in.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Setter
public class ProjectStats {
    private String id;
    private String name;
    private double focusedMinutes;
    private String description;
}
