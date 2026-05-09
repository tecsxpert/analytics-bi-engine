package com.internship.tool.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    private final JwtUtil jwtUtil =
            new JwtUtil();

    public JwtUtilTest() {

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
    void testGenerateToken() {

        String token =
                jwtUtil.generateToken("admin");

        assertNotNull(token);

        assertFalse(token.isEmpty());
    }

    @Test
    void testExtractUsername() {

        String token =
                jwtUtil.generateToken("admin");

        String username =
                jwtUtil.extractUsername(token);

        assertEquals(
                "admin",
                username
        );
    }

    @Test
    void testValidateToken() {

        String token =
                jwtUtil.generateToken("admin");

        boolean valid =
                jwtUtil.validateToken(token);

        assertTrue(valid);
    }
}