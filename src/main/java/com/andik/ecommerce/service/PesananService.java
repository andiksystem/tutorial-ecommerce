package com.andik.ecommerce.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.andik.ecommerce.entity.Pengguna;
import com.andik.ecommerce.entity.Pesanan;
import com.andik.ecommerce.entity.PesananItem;
import com.andik.ecommerce.entity.Produk;
import com.andik.ecommerce.exception.BadRequestException;
import com.andik.ecommerce.exception.ResourceNotFoundException;
import com.andik.ecommerce.model.KeranjangRequest;
import com.andik.ecommerce.model.PesananRequest;
import com.andik.ecommerce.model.PesananResponse;
import com.andik.ecommerce.model.StatusPesanan;
import com.andik.ecommerce.repository.PesananItemRepository;
import com.andik.ecommerce.repository.PesananRepository;
import com.andik.ecommerce.repository.ProdukRepository;

@Service
public class PesananService {

    @Autowired
    private ProdukRepository produkRepository;
    @Autowired
    private PesananRepository pesananRepository;
    @Autowired
    private PesananItemRepository pesananItemRepository;
    @Autowired
    private KeranjangService keranjangService;
    @Autowired
    private PesananLogService pesananLogService;

    @Transactional
    public PesananResponse create(String username, PesananRequest request) {
        Pesanan pesanan = new Pesanan();
        pesanan.setId(UUID.randomUUID().toString());
        pesanan.setTanggal(new Date());
        pesanan.setNomor(generateNomorPesanan());
        pesanan.setPengguna(new Pengguna(username));
        pesanan.setAlamatPengiriman(request.getAlamatPengiriman());
        pesanan.setStatusPesanan(StatusPesanan.DRAFT);
        pesanan.setWaktuPesan(new Date());

        List<PesananItem> items = new ArrayList<>();
        for (KeranjangRequest k : request.getItems()) {
            Produk produk = produkRepository.findById(k.getProdukId())
                    .orElseThrow(() -> new BadRequestException("Produk ID " + k.getProdukId() + " tidak ditemukan"));
            if (produk.getStok() < k.getKuantitas()) {
                throw new BadRequestException("Stok tidak mencukupi");
            }

            PesananItem pi = new PesananItem();
            pi.setId(UUID.randomUUID().toString());
            pi.setProduk(produk);
            pi.setDeskripsi(produk.getNama());
            pi.setKuantitas(k.getKuantitas());
            pi.setHarga(produk.getHarga());
            pi.setJumlah(new BigDecimal(pi.getHarga().doubleValue() * pi.getKuantitas()));
            pi.setPesanan(pesanan);
            items.add(pi);
        }

        BigDecimal jumlah = BigDecimal.ZERO;
        for (PesananItem pesananItem : items) {
            jumlah = jumlah.add(pesananItem.getJumlah());
        }

        pesanan.setJumlah(jumlah);
        pesanan.setOngkir(request.getOngkir());
        pesanan.setTotal(pesanan.getJumlah().add(pesanan.getOngkir()));

        Pesanan saved = pesananRepository.save(pesanan);
        for (PesananItem pesananItem : items) {
            pesananItemRepository.save(pesananItem);
            Produk produk = pesananItem.getProduk();
            produk.setStok(produk.getStok() - pesananItem.getKuantitas());
            produkRepository.save(produk);
            keranjangService.delete(username, produk.getId());
        }

        // catat log
        pesananLogService.createLog(username, pesanan, PesananLogService.DRAFT, "Pesanan sukses dibuat");
        PesananResponse pesananResponse = new PesananResponse(saved, items);
        return pesananResponse;

    }

    @Transactional
    public Pesanan cancelPesanan(String pesananId, String userId) {
        Pesanan pesanan = pesananRepository.findById(pesananId)
                .orElseThrow(() -> new ResourceNotFoundException("Pesanan ID " + pesananId + " tidak ditemukan"));
        if (!userId.equals(pesanan.getPengguna().getId())) {
            throw new BadRequestException("Pesanan ini hanya dapat dibatalkan oleh yang bersangkutan");
        }

        if (!StatusPesanan.DRAFT.equals(pesanan.getStatusPesanan())) {
            throw new BadRequestException("Pesanan ini tidak dapat dibatalkan karena sudah diproses");
        }

        pesanan.setStatusPesanan(StatusPesanan.DIBATALKAN);
        Pesanan saved = pesananRepository.save(pesanan);
        pesananLogService.createLog(userId, saved, PesananLogService.DIBATALKAN, "Pesanan sukses dibatalkan");
        return saved;
    }

    @Transactional
    public Pesanan terimaPesanan(String pesananId, String userId) {
        Pesanan pesanan = pesananRepository.findById(pesananId)
                .orElseThrow(() -> new ResourceNotFoundException("Pesanan ID " + pesananId + " tidak ditemukan"));
        if (!userId.equals(pesanan.getPengguna().getId())) {
            throw new BadRequestException("Pesanan ini hanya dapat dibatalkan oleh yang bersangkutan");
        }

        if (!StatusPesanan.PENGIRIMAN.equals(pesanan.getStatusPesanan())) {
            throw new BadRequestException(
                    "Penerimaan gagal, status pesanan saat ini adalah " + pesanan.getStatusPesanan().name());
        }

        pesanan.setStatusPesanan(StatusPesanan.DIBATALKAN);
        Pesanan saved = pesananRepository.save(pesanan);
        pesananLogService.createLog(userId, saved, PesananLogService.DIBATALKAN, "Pesanan sukses dibatalkan");
        return saved;
    }

    public List<Pesanan> findAllPesananUser(String userId, int page, int limit) {
        return pesananRepository.findByPenggunaId(userId,
                PageRequest.of(page, limit, Sort.by("waktuPesan").descending()));
    }

    public List<Pesanan> search(String filterText, int page, int limit) {
        return pesananRepository.search(filterText.toLowerCase(),
                PageRequest.of(page, limit, Sort.by("waktuPesan").descending()));
    }

    private String generateNomorPesanan() {
        return String.format("%016d", System.nanoTime());
    }

    @Transactional
    public Pesanan konfirmasiPembayaran(String pesananId, String userId) {
        Pesanan pesanan = pesananRepository.findById(pesananId)
                .orElseThrow(() -> new ResourceNotFoundException("Pesanan ID " + pesananId + " tidak ditemukan"));

        if (!StatusPesanan.DRAFT.equals(pesanan.getStatusPesanan())) {
            throw new BadRequestException(
                    "Konfirmasi pesanan gagal, status pesanan saat ini adalah " + pesanan.getStatusPesanan().name());
        }

        pesanan.setStatusPesanan(StatusPesanan.PEMBAYARAN);
        Pesanan saved = pesananRepository.save(pesanan);
        pesananLogService.createLog(userId, saved, PesananLogService.PEMBAYARAN, "Pembayaran sukses dikonfirmasi");
        return saved;
    }

    @Transactional
    public Pesanan packing(String pesananId, String userId) {
        Pesanan pesanan = pesananRepository.findById(pesananId)
                .orElseThrow(() -> new ResourceNotFoundException("Pesanan ID " + pesananId + " tidak ditemukan"));

        if (!StatusPesanan.PEMBAYARAN.equals(pesanan.getStatusPesanan())) {
            throw new BadRequestException(
                    "Packing pesanan gagal, status pesanan saat ini adalah " + pesanan.getStatusPesanan().name());
        }

        pesanan.setStatusPesanan(StatusPesanan.PACKING);
        Pesanan saved = pesananRepository.save(pesanan);
        pesananLogService.createLog(userId, saved, PesananLogService.PACKING, "Pesanan sedang disiapkan");
        return saved;
    }

    @Transactional
    public Pesanan kirim(String pesananId, String userId) {
        Pesanan pesanan = pesananRepository.findById(pesananId)
                .orElseThrow(() -> new ResourceNotFoundException("Pesanan ID " + pesananId + " tidak ditemukan"));

        if (!StatusPesanan.PACKING.equals(pesanan.getStatusPesanan())) {
            throw new BadRequestException(
                    "Pengiriman pesanan gagal, status pesanan saat ini adalah " + pesanan.getStatusPesanan().name());
        }

        pesanan.setStatusPesanan(StatusPesanan.PENGIRIMAN);
        Pesanan saved = pesananRepository.save(pesanan);
        pesananLogService.createLog(userId, saved, PesananLogService.PENGIRIMAN, "Pesanan sedang dikirim");
        return saved;
    }

}
