// product/consumer/StockUpdateConsumer.java
package com.example.msa.product.consumer;

import com.example.msa.product.dto.ProductUpdateStockDTO;
import com.example.msa.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockUpdateConsumer {

    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "update-stock-topic", groupId = "product-group")
    public void consumeDecrease(String message) {
        try {
            ProductUpdateStockDTO dto = objectMapper.readValue(message, ProductUpdateStockDTO.class);
            productService.decreaseStock(dto.getProductId(), dto.getProductQuantity());
        } catch (Exception e) {
            // 재시도/DLQ 없음 — msa_test와 동일한 한계, 알려진 이슈로 문서화
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "restore-stock-topic", groupId = "product-group")
    public void consumeRestore(String message) {
        try {
            ProductUpdateStockDTO dto = objectMapper.readValue(message, ProductUpdateStockDTO.class);
            productService.increaseStock(dto.getProductId(), dto.getProductQuantity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}