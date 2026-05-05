package com.internship.tool.controller;

import com.internship.tool.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username,
                                        @RequestParam String password) {

        if ("admin".equals(username) && "password".equals(password)) {
            return ResponseEntity.ok(jwtUtil.generateToken(username));
        }

        return ResponseEntity.status(401).body("Invalid credentials");
    }
}