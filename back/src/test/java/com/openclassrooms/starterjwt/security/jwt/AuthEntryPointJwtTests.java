package com.openclassrooms.starterjwt.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthEntryPointJwtTests {

    @InjectMocks
    private AuthEntryPointJwt authEntryPointJwt;

    @Mock
    private AuthenticationException authenticationException;

    private MockHttpServletResponse httpServletResponse;
    private MockHttpServletRequest httpServletRequest;

    @BeforeEach
    public void setup() {
        authEntryPointJwt = new AuthEntryPointJwt();
        httpServletRequest = new MockHttpServletRequest("POST", "/api/session");
        httpServletResponse = new MockHttpServletResponse();
    }

    @Test
    public void testCommence_setsResponseStatusToUnauthorized() throws Exception {
        when(authenticationException.getMessage()).thenReturn("Authentication Exception");
        authEntryPointJwt.commence(httpServletRequest, httpServletResponse, authenticationException);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, httpServletResponse.getStatus());
    }

    @Test
    public void testCommence_setsResponseContentTypeToJson() throws Exception {
        when(authenticationException.getMessage()).thenReturn("Authentication Exception");
        authEntryPointJwt.commence(httpServletRequest, httpServletResponse, authenticationException);
        assertEquals("application/json", httpServletResponse.getContentType());
    }

    @Test
    public void testCommence_responseBodyContainsCorrectErrorMessage() throws Exception {
        when(authenticationException.getMessage()).thenReturn("Authentication Exception");
        authEntryPointJwt.commence(httpServletRequest, httpServletResponse, authenticationException);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseBody = mapper.readValue(httpServletResponse.getContentAsByteArray(), Map.class);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, responseBody.get("status"));
        assertEquals("Unauthorized", responseBody.get("error"));
        assertEquals("Authentication Exception", responseBody.get("message"));
    }

    @Test
    public void testCommence_responseBodyHasAllRequiredFields() throws Exception {
        when(authenticationException.getMessage()).thenReturn("Authentication Exception");
        authEntryPointJwt.commence(httpServletRequest, httpServletResponse, authenticationException);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseBody = mapper.readValue(httpServletResponse.getContentAsByteArray(), Map.class);

        assertTrue(responseBody.containsKey("status"));
        assertTrue(responseBody.containsKey("error"));
        assertTrue(responseBody.containsKey("message"));
        assertTrue(responseBody.containsKey("path"));
    }

    @Test
    public void testCommence_doesNotThrowExceptions() {
        when(authenticationException.getMessage()).thenReturn("Authentication Exception");
        assertDoesNotThrow(() ->
                        authEntryPointJwt.commence(httpServletRequest, httpServletResponse, authenticationException)
        );
    }
}
