package com.nathan.lock_in.chronos;

import com.nathan.lock_in.user.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/chronos")
@RequiredArgsConstructor
@Tag(
        name = "Chronos",
        description = "Endpoints for managing focus sessions (chronos) for the authenticated user"
)
public class ChronoController {

    private final ChronosService chronosService;

    @PostMapping("/")
    @Operation(
            summary = "Create a new chrono",
            description = "Creates a new focus session (chrono) for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Chrono successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Chronos.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data supplied to create a chrono"
            )
    })
    public ResponseEntity<?> save(@RequestBody ChronosCreationDTO toSave){
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
            String userId = user.getId();
            toSave.setUserId(userId);
            Chronos newChrono = chronosService.save(toSave);
            return new ResponseEntity<>(newChrono, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(400));
        }
    }

    @GetMapping("/")
    @Operation(
            summary = "List chronos for user",
            description = "Returns a paginated list of chronos (focus sessions) for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Chronos successfully retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Chronos.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination parameters or request"
            )
    })
    public ResponseEntity<?> findAll(
            @Parameter(description = "Optional page size", example = "20")
            @RequestParam(required = false) Integer size,
            @Parameter(description = "Optional page number (0-based)", example = "0")
            @RequestParam(required = false) Integer page
    ){
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assert auth != null;
            CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
            assert user != null;
            String userId = user.getId();
            List<Chronos> chronos = chronosService.findAll(userId, size, page);
            return new ResponseEntity<>(chronos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(400));
        }
    }

    @GetMapping("/dates")
    @Operation(
            summary = "Get chronos between dates",
            description = "Returns all chronos that start between the given `start` and `end` instants."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Chronos in the requested date range successfully retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Chronos.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error while fetching chronos for the date range"
            )
    })
    public ResponseEntity<?> getChronosBetweenDates(
            @Parameter(
                    description = "Start of the date range (ISO-8601)",
                    example = "2025-01-01T00:00:00Z"
            )
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant start,

            @Parameter(
                    description = "End of the date range (ISO-8601)",
                    example = "2025-01-31T23:59:59Z"
            )
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant end
    ){
        try{
            List<Chronos> chronos = chronosService.getChronosBetweenDates(start, end);
            return ResponseEntity.ok(chronos);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("/{chronoId}")
    @Operation(
            summary = "Delete a chrono",
            description = "Deletes a chrono by its identifier and returns the deleted entity."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Chrono successfully deleted",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Chronos.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error while deleting the chrono"
            )
    })
    public ResponseEntity<?> delete(
            @Parameter(description = "Identifier of the chrono to delete")
            @PathVariable String chronoId
    ){
        try {
            Chronos deleted = chronosService.delete(chronoId);
            return new ResponseEntity<>(deleted, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }

    @PatchMapping("/")
    @Operation(
            summary = "Update an existing chrono",
            description = "Updates fields of an existing chrono and returns the updated entity."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Chrono successfully updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Chronos.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error while updating the chrono"
            )
    })
    public ResponseEntity<?> edit(@RequestBody ChronosUpdate updatedChronos){
        try {
            Chronos updated = chronosService.update(updatedChronos);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
