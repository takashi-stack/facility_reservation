package com.example.facilityreservation.utility;

import java.time.LocalDateTime;

import com.example.facilityreservation.domain.entity.ReservationEntity;


public class MockReservationEntity {

    public static ReservationEntity genReservationEntity() {
        ReservationEntity en = new ReservationEntity();
        en.setId(1);
        en.setStartTime(LocalDateTime.of(2021, 1, 1, 12, 0));
        en.setEndTime(LocalDateTime.of(2021, 2, 1, 12, 0));
        en.setFacility(MockFacility.genFacilityEntity());
        en.setUser(MockUser.genMockRootUserEntity());
        return en;
    }
}
