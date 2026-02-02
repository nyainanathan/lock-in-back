package com.nathan.lock_in.auth;

import com.nathan.lock_in.auth.jwt.JwtUtil;
import com.nathan.lock_in.user.UserCreationDTO;
import com.nathan.lock_in.user.UserRepository;
import com.nathan.lock_in.user.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    private LoginResponse register(UserCreationDTO userToSave){

        Users isExisting = userRepo.findByEmail(userToSave.getEmail());

        if(isExisting != null) {
            throw new RuntimeException("This user already exists!");
        }

        userToSave.setPasswordHash(
                passwordEncoder.encode(userToSave.getPasswordHash())
        );

        Users createdUser = userRepo.save(userToSave);

        String jwtToken = jwtUtil.createToken(Map.of(), createdUser.getEmail());

        MinimalUserInfo userInfo = new MinimalUserInfo();
        userInfo.setId(createdUser.getId());
        userInfo.setEmail(createdUser.getEmail());
        userInfo.setFirstName(createdUser.getFirstName());
        userInfo.setLastName(createdUser.getLastName());

        return new LoginResponse(
                jwtToken,
                userInfo
        );

    }
}
