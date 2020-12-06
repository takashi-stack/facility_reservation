package com.example.facilityreservation.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.facilityreservation.domain.entity.FacilityEntity;

public interface FacilityRepository extends
        JpaRepository<FacilityEntity, Integer> {

    public void deleteByIdGreaterThan(int id);
}
