package org.scoula.merchant.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.merchant.dto.MerchantCategoryDTO;
import org.scoula.merchant.dto.MerchantDTO;
import org.scoula.merchant.service.MerchantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/merchants")
public class MerchantController {
    final MerchantService service;

    @GetMapping("/{id}")
    public ResponseEntity<MerchantDTO> getMerchant(@PathVariable("id") int id) {
        return ResponseEntity.ok(service.getMerchant(id));
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<MerchantCategoryDTO> getMerchantCategory(@PathVariable("id") int id) {
        return ResponseEntity.ok(service.getMerchantCategory(id));
    }

    @PostMapping("")
    public ResponseEntity<Void> createMerchant(@RequestBody MerchantDTO dto) {
        service.createMerchant(dto);
        return ResponseEntity.ok().build();
    }
}
