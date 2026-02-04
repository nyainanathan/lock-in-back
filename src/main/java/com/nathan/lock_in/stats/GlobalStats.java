package com.nathan.lock_in.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class GlobalStats {
    private Double focusedMinutes;
    private Double pause;
    private Integer sessionsCount;
    private Double focusRatio;
    private Double averageSessionsMinutes;
}
