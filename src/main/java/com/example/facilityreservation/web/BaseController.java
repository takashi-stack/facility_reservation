package com.example.facilityreservation.web;

import java.security.Principal;

import org.springframework.security.core.Authentication;

import com.example.facilityreservation.domain.entity.UserEntity;

public class BaseController {

    protected UserEntity getLoginUser(Principal principal) {
        Authentication authentication = (Authentication) principal;
        return (UserEntity) authentication.getPrincipal();
    }
}
