package com.example.facilityreservation.web;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class BaseControllerTest {
    
    private BaseController target = new BaseController();

    @Test
    public void getLoginUserTest() throws Exception {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        target.getLoginUser((Principal) authentication);
        verify(authentication, times(1)).getPrincipal();
    }
}
