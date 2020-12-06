package com.example.facilityreservation.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PermissionLevel {
    NONE("未設定"), ADMIN("管理者"), USER("ユーザ");

    private String text;
}
