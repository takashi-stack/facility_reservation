package com.example.facilityreservation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.facilityreservation.domain.entity.UserEntity;
import com.example.facilityreservation.domain.repository.UserRepository;

/**
 * ユーザ認証用のサービス
 *
 */
@Service
public class UserAuthService implements UserDetailsService {
    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        if (username == null || "".equals(username)) {
            throw new UsernameNotFoundException("Username is empty");
        }

        UserEntity user = repository.findById(username).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("User not found for name: " + username);
        }

        return user;
    }

}
