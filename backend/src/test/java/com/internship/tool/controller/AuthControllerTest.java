package com.internship.tool.controller;

import com.internship.tool.security.JwtUtil;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthControllerTest {

    private final JwtUtil jwtUtil =
            new JwtUtil();

    public AuthControllerTest() {

        try {

            java.lang.reflect.Field secretField =
                    JwtUtil.class.getDeclaredField("SECRET");

            secretField.setAccessible(true);

            secretField.set(
                    jwtUtil,
                    "mysecretkeymysecretkeymysecretkey"
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Test
    void testTokenGenerationForAdmin() {

        String token =
                jwtUtil.generateToken("admin");

        assertNotNull(token);

        assertFalse(token.isEmpty());
    }

    @Test
    void testGeneratedTokenValidation() {

        String token =
                jwtUtil.generateToken("admin");

        boolean valid =
                jwtUtil.validateToken(token);

        assertTrue(valid);
    }
}