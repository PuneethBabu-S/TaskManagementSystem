package pbs.tms.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pbs.tms.dto.LoginRequest;
import pbs.tms.entity.Role;
import pbs.tms.entity.User;
import pbs.tms.repository.UserRepository;

import java.util.Optional;
import pbs.tms.utility.JwtUtil;

@Service
public interface AuthService {

    public String registerUser(User user);

    public Optional<User> getUserByUsername(String username);

    public String loginUser(LoginRequest loginRequest);
}