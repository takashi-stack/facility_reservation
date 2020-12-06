package com.example.facilityreservation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.facilityreservation.domain.entity.FacilityTypeEntity;
import com.example.facilityreservation.domain.repository.FacilityTypeRepository;

@Service
@Transactional(readOnly = true)
public class FacilityTypeService {

    @Autowired
    private FacilityTypeRepository facilityTypeRepository;

    public List<FacilityTypeEntity> list() {
        return facilityTypeRepository.findAll();
    }
}
