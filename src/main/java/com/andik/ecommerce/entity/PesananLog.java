package com.andik.ecommerce.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Data
@Entity
public class PesananLog implements Serializable {

    @Id
    private String id;
    @JoinColumn
    @ManyToOne
    private Pesanan pesanan;
    @JoinColumn
    @ManyToOne
    private Pengguna pengguna;
    private Integer logType;
    private String logMessage;
    @Temporal(TemporalType.TIMESTAMP)
    private Date waktu;
}
