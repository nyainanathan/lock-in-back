package com.nathan.lock_in.chronos;

import com.nathan.lock_in.user.CustomUserDetails;
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
public class ChronoController {

    private final ChronosService chronosService;

    @PostMapping("/")
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
    public ResponseEntity<?> findAll(@RequestParam(required = false) Integer size, @RequestParam(required = false) Integer page){
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
    public ResponseEntity<?> getChronosBetweenDates(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant start,

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
    public ResponseEntity<?> delete(@PathVariable String chronoId){
        try {
            Chronos deleted = chronosService.delete(chronoId);
            return new ResponseEntity<>(deleted, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }

    @PatchMapping("/")
    public ResponseEntity<?> edit(@RequestBody ChronosUpdate updatedChronos){
        try {
            Chronos updated = chronosService.update(updatedChronos);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
