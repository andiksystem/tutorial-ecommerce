package com.andik.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.andik.ecommerce.entity.Pesanan;

public interface PesananItemRepository extends JpaRepository<Pesanan, String> {

}
