package com.andik.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andik.ecommerce.entity.Pengguna;
import com.andik.ecommerce.service.PenggunaService;

@RestController
@RequestMapping("/api")
public class PenggunaController {

    @Autowired
    private PenggunaService penggunaService;

    @GetMapping("/penggunas")
    public List<Pengguna> findAll() {
        return penggunaService.findAll();
    }

    @GetMapping("/penggunas/{id}")
    public Pengguna findById(@PathVariable("id") String id) {
        return penggunaService.findById(id);
    }

    @PostMapping("/penggunas")
    public Pengguna create(@RequestBody Pengguna pengguna) {
        return penggunaService.create(pengguna);
    }

    @PutMapping("/penggunas")
    public Pengguna edit(@RequestBody Pengguna pengguna) {
        return penggunaService.edit(pengguna);
    }

    @DeleteMapping("/penggunas/{id}")
    public void deleteById(@PathVariable("id") String id) {
        penggunaService.deleteById(id);
    }

}
