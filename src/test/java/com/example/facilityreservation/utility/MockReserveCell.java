package com.example.facilityreservation.utility;

import java.util.ArrayList;
import java.util.List;

import com.example.facilityreservation.domain.entity.ReservationEntity;
import com.example.facilityreservation.web.bean.ReserveCell;


public class MockReserveCell {

    public static ReserveCell genReserveCell(int year, int month, int day) {
        ReserveCell cell = new ReserveCell();
        cell.setYear(year);
        cell.setMonth(month);
        cell.setDay(day);
        cell.setFacility(MockFacility.genFacilityEntity());
        List<ReservationEntity> reserves = new ArrayList<>();
        reserves.add(MockReservationEntity.genReservationEntity());
        cell.setReserves(reserves);
        return cell;
    }
}
