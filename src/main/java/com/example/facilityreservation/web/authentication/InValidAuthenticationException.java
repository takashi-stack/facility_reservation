package com.example.facilityreservation.web.authentication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.AuthenticationException;

import lombok.Getter;

public class InValidAuthenticationException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    @Getter
    private Map<String, List<String>> errors = new HashMap<>();

    public InValidAuthenticationException(String msg) {
        super(msg);
    }

    public InValidAuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

    public void setError(String field, String message) {
        if (errors.containsKey(field)) {
            errors.get(field)
                .add(message);
        } else {
            List<String> list = new ArrayList<>();
            list.add(message);
            errors.put(field, list);
        }
    }

    public boolean hasError(String field) {
        return errors.containsKey(field);
    }

    public List<String> getErrors(String field) {
        return errors.get(field);
    }
}
