package com.andik.ecommerce.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Pengguna implements Serializable {

    @Id
    private String id;
    @JsonIgnore
    private String password;
    private String nama;
    @JsonIgnore
    private String alamat;
    @JsonIgnore
    private String email;
    @JsonIgnore
    private String hp;
    @JsonIgnore
    private String roles;
    @JsonIgnore
    private Boolean isAktif;

    public Pengguna(String username) {
        this.id = username;
    }

}
