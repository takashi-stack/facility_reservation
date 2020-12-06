package com.example.facilityreservation.web.authentication;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.example.facilityreservation.web.bean.LoginBean;

@Component
public class AuthenticationProviderImpl extends DaoAuthenticationProvider {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(
        AuthenticationProviderImpl.class);

    @Autowired
    private Validator validator;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials()
            .toString();

        // Validate
        LoginBean bean = new LoginBean(username, password);
        Set<ConstraintViolation<LoginBean>> result = validator.validate(bean);
        if (!result.isEmpty()) {
            InValidAuthenticationException exception = new InValidAuthenticationException("");
            for (ConstraintViolation<LoginBean> cv : result) {
                exception.setError(cv.getPropertyPath()
                    .toString(), cv.getMessage());
            }
            throw exception;
        }

        return super.authenticate(authentication);
    }

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        super.setUserDetailsService(userDetailsService);
    }
}
