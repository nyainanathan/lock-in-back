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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse register(UserCreationDTO userToSave){

        Optional<Users> isExisting = userRepo.findByEmail(userToSave.getEmail());
        System.out.println("this user");
        if(!isExisting.isEmpty()) {
            throw new RuntimeException("This user already exists!");
        }

        userToSave.setPasswordHash(
                passwordEncoder.encode(userToSave.getPasswordHash())
        );
        System.out.println("saving");
        Users createdUser = userRepo.save(userToSave);

        return getLoginResponse(createdUser);

    }

    public LoginResponse login(LoginRequest request) throws Exception {
        Optional<Users> currentUser = userRepo.findByEmail(request.getEmail());

        if(currentUser.isEmpty()) {
            throw new Exception("This email is associated with any account!");
        }

        if(!passwordEncoder.matches(request.getPassword(), currentUser.get().getPasswordHash())){
            throw new Exception("Wrong password!");
        }

        return getLoginResponse(currentUser.get());
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
