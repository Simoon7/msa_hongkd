// product/service/ProductService.java
package com.example.msa.product.service;

import com.example.msa.product.domain.Product;
import com.example.msa.product.dto.*;
import com.example.msa.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public Long register(ProductRegisterReqDTO dto, Long memberId) {
        return productRepository.save(dto.toEntity(memberId)).getId();
    }

    @Transactional(readOnly = true)
    public List<ProductResDTO> list(String keyword) {
        List<Product> products = (keyword == null || keyword.isBlank())
                ? productRepository.findAll()
                : productRepository.findByNameContaining(keyword);
        return products.stream().map(ProductResDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public ProductResDTO detail(Long productId) {
        return new ProductResDTO(findProduct(productId));
    }

    public void update(Long productId, ProductUpdateReqDTO dto) {
        Product product = findProduct(productId);
        product.update(dto.getName(), dto.getCategory(), dto.getPrice(),
                dto.getStockQuantity(), dto.getDescription());
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다."));
    }

    public void decreaseStock(Long productId, int quantity) {
        Product product = findProduct(productId);
        product.decreaseStock(quantity);
    }

    public void increaseStock(Long productId, int quantity) {
        Product product = findProduct(productId);
        product.increaseStock(quantity);
    }
}