package com.nathan.lock_in.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StreakStats {
    private String idUser;
    private Integer biggestStreak;
    private Integer currentStreak;
}
