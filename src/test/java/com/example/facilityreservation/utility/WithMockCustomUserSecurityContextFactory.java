package com.example.facilityreservation.utility;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.example.facilityreservation.domain.entity.UserEntity;
import com.example.facilityreservation.domain.enums.PermissionLevel;


public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UserEntity principal = new UserEntity();
        principal.setPassword(customUser.password());
        principal.setId(customUser.username());

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : customUser.roles()) {
            if (role.equals(PermissionLevel.NONE.toString())) {
                authorities.add(new SimpleGrantedAuthority("NONE"));
                principal.setPermissionLevel(PermissionLevel.NONE);
            }
            if (role.equals(PermissionLevel.ADMIN.toString())) {
                authorities.add(new SimpleGrantedAuthority("ADMIN"));
                principal.setPermissionLevel(PermissionLevel.ADMIN);
            }
            if (role.equals(PermissionLevel.USER.toString())) {
                authorities.add(new SimpleGrantedAuthority("USER"));
                principal.setPermissionLevel(PermissionLevel.USER);
            }
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(principal, "password",
                principal.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
