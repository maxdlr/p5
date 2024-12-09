package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthTokenFilterTests {

    private AuthTokenFilter authTokenFilter;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    public void setup() {
        authTokenFilter = new AuthTokenFilter();
        authTokenFilter.jwtUtils = jwtUtils;
        authTokenFilter.userDetailsService = userDetailsService;
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testDoFilterInternal_validJwt_setsAuthentication() throws ServletException, IOException {
        String jwtToken = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + jwtToken);
        when(jwtUtils.validateJwtToken(jwtToken)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(jwtToken)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(null);

        authTokenFilter.doFilterInternal(request, response, filterChain);

        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(userDetails, authentication.getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_missingJwt_doesNotSetAuthentication() throws ServletException, IOException {

        authTokenFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_invalidJwt_doesNotSetAuthentication() throws ServletException, IOException {
        String jwtToken = "invalid.jwt.token";
        request.addHeader("Authorization", "Bearer " + jwtToken);

        authTokenFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_jwtParsingFails_doesNotSetAuthentication() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer invalid.jwt.token");

        authTokenFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_noBearerPrefix_doesNotSetAuthentication() throws ServletException, IOException {
        request.addHeader("Authorization", "invalidtokenwithoutbearer");

        authTokenFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testParseJwt_validBearerToken_returnsToken() {
        request.addHeader("Authorization", "Bearer my.jwt.token");

        String token = authTokenFilter.parseJwt(request);

        assertEquals("my.jwt.token", token);
    }

    @Test
    public void testParseJwt_noBearerToken_returnsNull() {
        request.addHeader("Authorization", "InvalidTokenFormat");

        String token = authTokenFilter.parseJwt(request);

        assertNull(token);
    }

    @Test
    public void testDoFilterInternal_exceptionThrown_doesNotCrash() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer my.jwt.token");

        assertDoesNotThrow(() ->
                        authTokenFilter.doFilterInternal(request, response, filterChain)
        );

        verify(filterChain).doFilter(request, response);
    }
}
