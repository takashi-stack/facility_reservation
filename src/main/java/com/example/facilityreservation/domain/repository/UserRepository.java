package com.example.facilityreservation.domain.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.facilityreservation.domain.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    public UserEntity findByIdAndPassword(String id, String password);

    public void deleteByIdNotIn(Collection<String> ids);
}
