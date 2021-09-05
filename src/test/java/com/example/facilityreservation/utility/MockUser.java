package com.example.facilityreservation.utility;

import com.example.facilityreservation.domain.entity.UserEntity;
import com.example.facilityreservation.domain.enums.PermissionLevel;


public class MockUser {

    public static UserEntity genMockRootUserEntity() {
        UserEntity en = new UserEntity();
        en.setId("root");
        en.setPassword("admin00");
        en.setPermissionLevel(PermissionLevel.ADMIN);
        return en;
    }
}

