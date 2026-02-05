package com.nathan.lock_in.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/global")
    public ResponseEntity<?> getGlobalStats(
            @RequestParam(required = false) StatsRangeEnum range
    ) {
        try {
            GlobalStats stats = statsService.getGlobalStats(range);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching global stats: " + e.getMessage());
        }
    }

    @GetMapping("/trends")
    public ResponseEntity<?> getTrends(
            @RequestParam(required = false) Integer dayRange
    ) {
        try {
            FocusTrends[] trends = statsService.getTrends(dayRange);
            return ResponseEntity.ok(trends);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching trends: " + e.getMessage());
        }
    }

    @GetMapping("/projects")
    public ResponseEntity<?> getProjectStats() {
        try {
            List<ProjectStats> projectStats = statsService.getProjectStats();
            return ResponseEntity.ok(projectStats);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching project stats: " + e.getMessage());
        }
    }

    @GetMapping("/streak")
    public ResponseEntity<?> getUserStreakStats() {
        try {
            StreakStats streakStats = statsService.getUserStreakStats();
            return ResponseEntity.ok(streakStats);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching streak stats: " + e.getMessage());
        }
    }
}