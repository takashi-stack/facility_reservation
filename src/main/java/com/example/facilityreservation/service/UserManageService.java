package com.example.facilityreservation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.facilityreservation.domain.entity.UserEntity;
import com.example.facilityreservation.domain.repository.UserRepository;
import com.example.facilityreservation.exception.BusinessException;

@Service
@Transactional(readOnly = true)
public class UserManageService {
    @Autowired
    private UserRepository userRepository;

    public UserEntity findById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    public void saveConfirm(UserEntity user) throws BusinessException {
        if (userRepository.existsById(user.getId())) {
            throw new BusinessException(user.getUsername() + "はすでに存在します。");
        }
    }

    @Transactional
    public void save(UserEntity user) throws BusinessException {
        saveConfirm(user);
        userRepository.save(user);
    }

    public List<UserEntity> list() {
        return userRepository.findAll();
    }

    public void deleteConfirm(String id) throws BusinessException {
        if (!userRepository.existsById(id)) {
            throw new BusinessException(id + "は存在しません。");
        }
    }

    @Transactional
    public void deleteById(String id) throws BusinessException {
        deleteConfirm(id);
        userRepository.deleteById(id);
    }
}
