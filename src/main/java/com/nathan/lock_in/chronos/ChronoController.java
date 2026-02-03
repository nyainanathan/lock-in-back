package com.nathan.lock_in.chronos;

import com.nathan.lock_in.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.FilterOutputStream;
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
            CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
            String userId = user.getId();

            List<Chronos> chronos = chronosService.findAll(userId, size, page);
            return new ResponseEntity<>(chronos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(400));
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
