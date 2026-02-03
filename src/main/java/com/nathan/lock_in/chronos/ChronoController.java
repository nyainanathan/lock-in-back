package com.nathan.lock_in.chronos;

import com.nathan.lock_in.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chronos")
@RequiredArgsConstructor
public class ChronoController {

    private final ChronosService chronosService;

    @PostMapping("/")
    public ResponseEntity<?> save(@RequestBody ChronosCreationDTO toSave){
        try {
            System.out.println("in the controller");
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
            String userId = user.getId();
            System.out.println(userId);
            toSave.setUserId(userId);
            System.out.println(toSave.getUnit());
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
}
