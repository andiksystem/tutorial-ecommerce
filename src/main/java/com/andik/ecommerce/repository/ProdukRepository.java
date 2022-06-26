package com.andik.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.andik.ecommerce.entity.Produk;

public interface ProdukRepository extends JpaRepository<Produk, String> {

}
