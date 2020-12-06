package com.example.facilityreservation.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.facilityreservation.domain.entity.FacilityEntity;
import com.example.facilityreservation.domain.entity.FacilityTypeEntity;
import com.example.facilityreservation.domain.entity.UserEntity;
import com.example.facilityreservation.domain.enums.PermissionLevel;
import com.example.facilityreservation.domain.repository.FacilityRepository;
import com.example.facilityreservation.domain.repository.FacilityTypeRepository;
import com.example.facilityreservation.domain.repository.ReservationRepository;
import com.example.facilityreservation.domain.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class InitService {
    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private FacilityTypeRepository facilityTypeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void initData() {
        // Reservationはすべて削除
        reservationRepository.deleteAll();

        // FacilityTypeは編集不可のため、何もしない

        // Facilityは、IDが5以上をすべて削除
        facilityRepository.deleteByIdGreaterThan(4);

        // Userは、IDが'root'と'ginza'以外をすべて削除
        userRepository.deleteByIdNotIn(Arrays.asList("root", "ginza"));

        // 初期ユーザの復元
        UserEntity root = new UserEntity("root", "admin00", PermissionLevel.ADMIN, "何でもできる");
        UserEntity ginga = new UserEntity("ginga", "soft00", PermissionLevel.USER, "施設情報、ユーザー情報機能は使えません。施設予約のみ可能");
        userRepository.saveAll(Arrays.asList(root, ginga));

        // 会議室の復元
        FacilityEntity facility1 = setInitFacility(1, "会議室001", 1, 20);
        FacilityEntity facility2 = setInitFacility(2, "会議室002", 1, 40);
        FacilityEntity facility3 = setInitFacility(3, "会議室003", 1, 20);
        FacilityEntity facility4 = setInitFacility(4, "応接室001", 2, 10);
        facilityRepository.saveAll(Arrays.asList(
            facility1, facility2, facility3, facility4));
    }

    private FacilityEntity setInitFacility(Integer id, String name,
                                           Integer typeId, Integer capacity) {
        FacilityEntity entity = facilityRepository.findById(id).orElse(null);

        if (entity == null) {
            entity = new FacilityEntity();
            entity.setId(id);
        }

        entity.setName(name);
        FacilityTypeEntity type = facilityTypeRepository.findById(typeId).orElse(null);
        entity.setType(type);

        entity.setCapacity(capacity);

        return entity;
    }
}
