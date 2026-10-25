package com.example.msa.ordering.service;

import com.example.msa.ordering.dto.ProductUpdateStockDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockUpdateProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendStockDecrease(Long productId, int quantity) {
        send("update-stock-topic", productId, quantity);
    }

    public void sendStockRestore(Long productId, int quantity) {
        send("restore-stock-topic", productId, quantity);
    }

    private void send(String topic, Long productId, int quantity) {
        try {
            String json = objectMapper.writeValueAsString(new ProductUpdateStockDTO(productId, quantity));
            kafkaTemplate.send(topic, json);
        } catch (Exception e) {
            throw new IllegalStateException("재고 변경 메시지 발행 실패", e);
        }
    }
}