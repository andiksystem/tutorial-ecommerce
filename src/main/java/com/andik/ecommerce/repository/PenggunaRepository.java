package com.andik.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.andik.ecommerce.entity.Keranjang;

public interface PenggunaRepository extends JpaRepository<Keranjang, String> {

}
