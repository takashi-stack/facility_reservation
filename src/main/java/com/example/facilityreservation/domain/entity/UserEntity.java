package com.example.facilityreservation.domain.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.facilityreservation.domain.enums.PermissionLevel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Entity
@Table(name = "user")
public class UserEntity implements UserDetails {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 10)
    private String id;

    @Column(length = 20, nullable = false)
    private String password;

    @Column(length = 1, nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private PermissionLevel permissionLevel;

    @Column(length = 100)
    private String note;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(permissionLevel.toString()));
        return authorities;
    }

    @Override
    public String getUsername() {
        return id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setPermissionLevel(PermissionLevel permission) {
        permissionLevel = permission;
    }

    public void setPermissionLevel(Integer index) {
        permissionLevel = PermissionLevel.values()[index];
    }
}
