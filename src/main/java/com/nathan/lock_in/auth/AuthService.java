package com.nathan.lock_in.auth;

import com.nathan.lock_in.auth.jwt.JwtUtil;
import com.nathan.lock_in.user.UserCreationDTO;
import com.nathan.lock_in.user.UserRepository;
import com.nathan.lock_in.user.Users;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
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

        return getLoginResponse(createdUser);

    }

    private LoginResponse login(LoginRequest request) throws Exception {
        Users currentUser = userRepo.findByEmail(request.getEmail());

        if(currentUser == null) {
            throw new Exception("This email is associated with any account!");
        }

        if(!passwordEncoder.matches(request.getPassword(), currentUser.getPasswordHash())){
            throw new Exception("Wrong password!");
        }

        return getLoginResponse(currentUser);
    }

    @NonNull
    private LoginResponse getLoginResponse(Users currentUser) {
        String jwtToken = jwtUtil.createToken(Map.of(), currentUser.getEmail());

        MinimalUserInfo info = new MinimalUserInfo();
        info.setId(currentUser.getId());
        info.setEmail(currentUser.getEmail());
        info.setFirstName(currentUser.getFirstName());
        info.setLastName(currentUser.getLastName());

        return new LoginResponse(
                jwtToken,
                info
        );
    }
}
