package com.example.facilityreservation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.facilityreservation.domain.entity.FacilityEntity;
import com.example.facilityreservation.domain.entity.FacilityTypeEntity;
import com.example.facilityreservation.domain.entity.UserEntity;
import com.example.facilityreservation.domain.repository.FacilityRepository;
import com.example.facilityreservation.domain.repository.FacilityTypeRepository;
import com.example.facilityreservation.exception.BusinessException;
import com.example.facilityreservation.web.form.FacilityForm;

@Service
@Transactional(readOnly = true)
public class FacilityService {
    @Autowired
    private FacilityRepository facilityRepo;

    @Autowired
    private FacilityTypeRepository facilityTypeRepo;

    public FacilityEntity findById(int id) {
        return facilityRepo.findById(id).orElse(null);
    }

    @Transactional
    public void add(FacilityForm form, UserEntity user) {
        FacilityEntity entity = toEntity(form, user);

        facilityRepo.save(entity);
    }

    @Transactional
    public void update(FacilityForm form, UserEntity user, int id) throws BusinessException {
        updateConfirm(id);
        FacilityEntity entity = facilityRepo.findById(id).orElse(null);

        entity.setName(form.getName());
        entity.setCapacity(form.getCapacity());

        FacilityTypeEntity type = facilityTypeRepo.findById(form.getTypeId()).orElse(null);
        entity.setType(type);

        facilityRepo.save(entity);
    }

    public void updateConfirm(int id) throws BusinessException {
        if (!facilityRepo.existsById(id)) {
            throw new BusinessException("該当の施設情報は存在しません。");
        }
    }

    public List<FacilityEntity> list() {
        return facilityRepo.findAll();
    }

    @Transactional
    public void deleteById(int id) throws BusinessException {
        deleteConfirm(id);
        facilityRepo.deleteById(id);
    }

    public void deleteConfirm(int id) throws BusinessException {
        if (!facilityRepo.existsById(id)) {
            throw new BusinessException("該当の施設情報は存在しません。");
        }
    }

    private FacilityEntity toEntity(FacilityForm form, UserEntity user) {
        return toEntity(form, user, null);
    }

    private FacilityEntity toEntity(FacilityForm form, UserEntity user,
                                    Integer id) {
        FacilityEntity entity = new FacilityEntity();
        entity.setId(id);
        entity.setName(form.getName());
        entity.setCapacity(form.getCapacity());

        FacilityTypeEntity type = facilityTypeRepo.findById(form.getTypeId()).orElse(null);
        entity.setType(type);
        return entity;
    }
}
