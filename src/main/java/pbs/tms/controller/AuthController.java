package pbs.tms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pbs.tms.dto.LoginRequest;
import pbs.tms.entity.User;
import pbs.tms.service.AuthService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        return ResponseEntity.ok(authService.registerUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        return authService.loginUser(loginRequest) != null
                ? ResponseEntity.ok(authService.loginUser(loginRequest))
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }
}