package com.andik.ecommerce.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
public class Keranjang implements Serializable {

    @Id
    private String id;
    @JoinColumn
    @ManyToOne
    private Produk produk;
    @JoinColumn
    @ManyToOne
    private Pengguna pengguna;
    private Double kuantitas;
    private BigDecimal harga;
    private BigDecimal jumlah;
    @Temporal(TemporalType.TIMESTAMP)
    private Date waktuDibuat;
}
