package com.nathan.lock_in.stats;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FocusTrends {
    private LocalDate date;
    private Double focusedMinutes;
}
