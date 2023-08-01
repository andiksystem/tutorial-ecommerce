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
    private String alamat;
    private String email;
    private String hp;
    private String roles;
    private Boolean isAktif;

    public Pengguna(String username) {
        this.id = username;
    }

}
