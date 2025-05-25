package pbs.tms.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pbs.tms.dto.LoginRequest;
import pbs.tms.entity.Role;
import pbs.tms.entity.User;
import pbs.tms.repository.UserRepository;

import java.util.Optional;

import pbs.tms.service.AuthService;
import pbs.tms.utility.JwtUtil;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return "User already exists";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }
        userRepository.save(user);
        return "User registered successfully";
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public String loginUser(LoginRequest loginRequest) {
        Optional<User> dbUser = this.getUserByUsername(loginRequest.getUsername());

        if (dbUser.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), dbUser.get().getPassword())) {
            return jwtUtil.generateToken(loginRequest.getUsername());
        }

        return null;
    }
}