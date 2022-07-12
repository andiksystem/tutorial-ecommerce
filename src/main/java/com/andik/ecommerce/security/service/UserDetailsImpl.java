package com.andik.ecommerce.security.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import com.andik.ecommerce.entity.Pengguna;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class UserDetailsImpl implements UserDetails {

    private String username;
    private String email;
    private String nama;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String roles;

    public UserDetailsImpl(String username,
            String email,
            String nama,
            String password,
            String roles) {
        this.username = username;
        this.email = email;
        this.nama = nama;
        this.password = password;
        this.roles = roles;
    }

    public static UserDetailsImpl build(Pengguna pengguna) {
        return new UserDetailsImpl(pengguna.getId(),
                pengguna.getEmail(),
                pengguna.getNama(),
                pengguna.getPassword(),
                pengguna.getRoles());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (StringUtils.hasText(roles)) {
            String[] splits = roles.replaceAll(" ", "").split(",");
            for (String string : splits) {
                authorities.add(new SimpleGrantedAuthority(string));
            }
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return true;
    }

}
