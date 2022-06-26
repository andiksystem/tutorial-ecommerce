package com.andik.ecommerce.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andik.ecommerce.entity.Produk;
import com.andik.ecommerce.exception.ResourceNotFoundException;
import com.andik.ecommerce.repository.ProdukRepository;

@Service
public class ProdukService {

    @Autowired
    private ProdukRepository produkRepository;

    public List<Produk> findAll() {
        return produkRepository.findAll();
    }

    public Produk findById(String id) {
        return produkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produk dengan id " + id + " tidak ditemukan"));
    }

    public Produk create(Produk produk) {
        produk.setId(UUID.randomUUID().toString());
        return produkRepository.save(produk);
    }

    public Produk edit(Produk produk) {
        return produkRepository.save(produk);
    }

    public Produk ubahGambar(String id, String gambar) {
        Produk produk = findById(id);
        produk.setGambar(gambar);
        return produkRepository.save(produk);
    }

    public void deleteById(String id) {
        produkRepository.deleteById(id);
    }
}
