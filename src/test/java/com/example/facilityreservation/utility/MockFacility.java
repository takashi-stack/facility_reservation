package com.example.facilityreservation.utility;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.example.facilityreservation.domain.entity.FacilityEntity;
import com.example.facilityreservation.domain.entity.FacilityTypeEntity;
import com.example.facilityreservation.domain.entity.UserEntity;


public class MockFacility {

    private static FacilityTypeEntity getFacilityTypeEntity() {
        UserEntity userEntity = new UserEntity();
        FacilityTypeEntity mockEntity = new FacilityTypeEntity();
        mockEntity.setId(1);
        mockEntity.setName("root");
        mockEntity.setInsertDate(LocalTime.of(12, 0));
        mockEntity.setUpdateDate(LocalTime.of(13, 0));
        mockEntity.setUser(userEntity);
        return mockEntity;
    }

    public static List<FacilityTypeEntity> genMockFacilityTypes() {
        List<FacilityTypeEntity> mockValue = new ArrayList<FacilityTypeEntity>();
        mockValue.add(getFacilityTypeEntity());
        return mockValue;
    }
    
    public static List<FacilityEntity> genFacilityEntitys() {
        List<FacilityEntity> mockValue = new ArrayList<FacilityEntity>();
        mockValue.add(genFacilityEntity());
        return mockValue;
    }
    
    public static FacilityEntity genFacilityEntity() {
        FacilityTypeEntity fte = getFacilityTypeEntity();
        FacilityEntity mockEntity = new FacilityEntity();
        mockEntity.setId(1);
        mockEntity.setName("root");
        mockEntity.setInsertDate(LocalTime.of(12, 0));
        mockEntity.setUpdateDate(LocalTime.of(13, 0));
        mockEntity.setUser(fte.getUser());
        mockEntity.setType(fte);
        return mockEntity;
    }
}
