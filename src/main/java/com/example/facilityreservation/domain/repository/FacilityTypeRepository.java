package com.example.facilityreservation.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.facilityreservation.domain.entity.FacilityTypeEntity;

public interface FacilityTypeRepository extends
        JpaRepository<FacilityTypeEntity, Integer> {

}
