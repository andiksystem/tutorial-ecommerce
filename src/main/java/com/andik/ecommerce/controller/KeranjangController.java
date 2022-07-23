package com.andik.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.andik.ecommerce.entity.Keranjang;
import com.andik.ecommerce.model.KeranjangRequest;
import com.andik.ecommerce.security.service.UserDetailsImpl;
import com.andik.ecommerce.service.KeranjangService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api")
@PreAuthorize("isAuthenticated()")
public class KeranjangController {

    @Autowired
    private KeranjangService keranjangService;

    @GetMapping("/keranjangs")
    public List<Keranjang> findByPenggunaId(@AuthenticationPrincipal UserDetailsImpl user) {
        return keranjangService.findByPenggunaId(user.getUsername());
    }

    @PostMapping("/keranjangs")
    public Keranjang create(@AuthenticationPrincipal UserDetailsImpl user, @RequestBody KeranjangRequest request) {
        return keranjangService.addKeranjang(user.getUsername(), request.getProdukId(), request.getKuantitas());
    }

    @PatchMapping("/keranjangs/{produkId}")
    public Keranjang update(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable("produkId") String produkId,
            @RequestParam("kuantitas") Double kuantitas) {
        return keranjangService.updateKuantitas(user.getUsername(), produkId, kuantitas);
    }

    @DeleteMapping("/keranjangs/{produkId}")
    public void delete(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable("produkId") String produkId) {
        keranjangService.delete(user.getUsername(), produkId);
    }

}
