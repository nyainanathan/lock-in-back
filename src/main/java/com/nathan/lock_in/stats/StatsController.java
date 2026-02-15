package com.nathan.lock_in.stats;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@Tag(
        name = "Statistics",
        description = "Endpoints for retrieving focus and productivity statistics"
)
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/global")
    @Operation(
            summary = "Get global statistics",
            description = "Returns aggregated global statistics for the authenticated user. "
                    + "Optionally filtered by a predefined time range (e.g. DAY, WEEK, MONTH)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Global statistics successfully retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GlobalStats.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error while fetching global statistics"
            )
    })
    public ResponseEntity<?> getGlobalStats(
            @Parameter(
                    description = "Optional time range for which to compute global stats",
                    required = false
            )
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
    @Operation(
            summary = "Get focus trends",
            description = "Returns the evolution of focus statistics over time. "
                    + "Optionally restricted to a given number of days."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Focus trends successfully retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FocusTrends[].class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error while fetching focus trends"
            )
    })
    public ResponseEntity<?> getTrends(
            @Parameter(
                    description = "Optional day range (number of past days to include in the trend)",
                    required = false,
                    example = "30"
            )
            @RequestParam(name = "range") Integer dayRange
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

    @GetMapping("/last-project")
    @Operation(
        summary = "Get the last worked on project stats",
        description = "Returns aggregated statistics grouped by project for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Project statistics successfully retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectStats.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error while fetching project statistics"
            )
    })
    public ResponseEntity<?> getLastProject(){
        try{
                ProjectStats lastProject = this.statsService.getLastWorkedOnProject();
                return new ResponseEntity<>(lastProject, HttpStatus.OK);
        } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/projects")
    @Operation(
            summary = "Get per-project statistics",
            description = "Returns aggregated statistics grouped by project for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Project statistics successfully retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectStats.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error while fetching project statistics"
            )
    })
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
    @Operation(
            summary = "Get user streak statistics",
            description = "Returns information about the user's current and longest productivity streaks."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Streak statistics successfully retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StreakStats.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error while fetching streak statistics"
            )
    })
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