package com.example.facilityreservation.web.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.facilityreservation.domain.entity.FacilityEntity;
import com.example.facilityreservation.domain.entity.ReservationEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReserveCell implements Serializable {
    private static final long serialVersionUID = 1L;

    private int year;
    private int month;
    public int day;
    private FacilityEntity facility;

    private List<ReservationEntity> reserves = new ArrayList<>();
}
