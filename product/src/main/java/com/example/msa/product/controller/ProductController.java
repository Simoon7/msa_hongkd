// product/controller/ProductController.java
package com.example.msa.product.controller;

import com.example.msa.product.dto.*;
import com.example.msa.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<Long> create(@RequestBody ProductRegisterReqDTO dto,
                                       @RequestHeader("X-User-Id") Long memberId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.register(dto, memberId));
    }

    @GetMapping("/list")
    public ResponseEntity<List<ProductResDTO>> list(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(productService.list(keyword));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResDTO> detail(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.detail(productId));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Void> update(@PathVariable Long productId, @RequestBody ProductUpdateReqDTO dto) {
        productService.update(productId, dto);
        return ResponseEntity.ok().build();
    }
}