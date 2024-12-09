package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.lang.reflect.Field;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetailsImpl userDetails;

    private final String secretKey = "testSecretKey";
    private final int expirationMs = 1000 * 60 * 60; // 1 hour in milliseconds

    @BeforeEach
    public void setUp() throws Exception {
        // Set private fields jwtSecret and jwtExpirationMs using reflection
        setPrivateField(jwtUtils, "jwtSecret", secretKey);
        setPrivateField(jwtUtils, "jwtExpirationMs", expirationMs);
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    public void testGenerateJwtToken_success() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");

        // Act
        String token = jwtUtils.generateJwtToken(authentication);

        // Assert
        assertNotNull(token, "JWT token should not be null");
        assertDoesNotThrow(() -> jwtUtils.getUserNameFromJwtToken(token));
        assertEquals("testuser", jwtUtils.getUserNameFromJwtToken(token),
                "Username extracted from the token should match the user principal's username");
    }

    @Test
    public void testGetUserNameFromJwtToken_success() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtUtils.generateJwtToken(authentication);

        // Act
        String username = jwtUtils.getUserNameFromJwtToken(token);

        // Assert
        assertEquals("testuser", username, "The extracted username should be 'testuser'");
    }

    @Test
    public void testValidateJwtToken_success() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtUtils.generateJwtToken(authentication);

        // Act
        boolean isValid = jwtUtils.validateJwtToken(token);

        // Assert
        assertTrue(isValid, "The JWT token should be valid");
    }

    @Test
    public void testValidateJwtToken_expiredToken() throws Exception {
        // Arrange
        // Use a short-lived token to ensure it expires quickly
        setPrivateField(jwtUtils, "jwtExpirationMs", 1); // 1 millisecond expiration
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtUtils.generateJwtToken(authentication);

        // Wait for the token to expire
        Thread.sleep(5);

        // Act
        boolean isValid = jwtUtils.validateJwtToken(token);

        // Assert
        assertFalse(isValid, "The JWT token should be expired");
    }

    @Test
    public void testValidateJwtToken_invalidSignature() {
        // Arrange
        String token = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + expirationMs))
                .signWith(SignatureAlgorithm.HS512, "wrongSecretKey")
                .compact();

        // Act
        boolean isValid = jwtUtils.validateJwtToken(token);

        // Assert
        assertFalse(isValid, "The JWT token should be invalid due to incorrect signature");
    }

    @Test
    public void testValidateJwtToken_malformedToken() {
        // Arrange
        String malformedToken = "this.is.not.a.valid.jwt.token";

        // Act
        boolean isValid = jwtUtils.validateJwtToken(malformedToken);

        // Assert
        assertFalse(isValid, "The JWT token should be invalid because it is malformed");
    }

    @Test
    public void testValidateJwtToken_unsupportedToken() {
        // Arrange
        String token = Jwts.builder()
                .setPayload("invalid payload without header")
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();

        // Act
        boolean isValid = jwtUtils.validateJwtToken(token);

        // Assert
        assertFalse(isValid, "The JWT token should be invalid as it's unsupported");
    }

    @Test
    public void testValidateJwtToken_emptyToken() {
        // Act
        boolean isValid = jwtUtils.validateJwtToken("");

        // Assert
        assertFalse(isValid, "The JWT token should be invalid if it is empty");
    }

    @Test
    public void testValidateJwtToken_nullToken() {
        // Act
        boolean isValid = jwtUtils.validateJwtToken(null);

        // Assert
        assertFalse(isValid, "The JWT token should be invalid if it is null");
    }
}
