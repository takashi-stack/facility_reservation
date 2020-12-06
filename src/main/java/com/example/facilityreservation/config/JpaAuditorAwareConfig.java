package com.example.facilityreservation.config;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.facilityreservation.domain.entity.UserEntity;

@Configuration
@EnableJpaAuditing
public class JpaAuditorAwareConfig {

    @Bean
    public AuditorAware<UserEntity> auditorAware() {
        return new SpringSecurityAuditor();
    }

    public static class SpringSecurityAuditor implements
            AuditorAware<UserEntity> {

        @Override
        public Optional<UserEntity> getCurrentAuditor() {
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication authentication = context.getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            }

            UserEntity userEntity = (UserEntity) authentication.getPrincipal();
            return Optional.of(userEntity);
        }

    }
}
