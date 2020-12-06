package com.example.facilityreservation.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.facilityreservation.domain.entity.FacilityEntity;
import com.example.facilityreservation.domain.entity.ReservationEntity;

public interface ReservationRepository extends
        JpaRepository<ReservationEntity, Integer> {

    public List<ReservationEntity> findByFacilityAndStartTimeBetween(FacilityEntity facility,
                                                                     LocalDateTime start,
                                                                     LocalDateTime end);

    @Query("select r from ReservationEntity r where r.facility = :facility AND ((r.startTime <= :start AND :start < r.endTime) OR (r.startTime < :end AND :end < r.endTime))")
    public List<ReservationEntity> findDuplicate(@Param("facility") FacilityEntity facility,
                                                 @Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);
}
