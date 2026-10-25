package com.example.msa.ordering.service;

import com.example.msa.ordering.client.ProductFeignClient;
import com.example.msa.ordering.domain.OrderStatus;
import com.example.msa.ordering.domain.Ordering;
import com.example.msa.ordering.dto.*;
import com.example.msa.ordering.repository.OrderingRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderingService {

    private final OrderingRepository orderingRepository;
    private final ProductFeignClient productFeignClient;
    private final StockUpdateProducer stockUpdateProducer;

    public OrderResDTO createOrder(Long memberId, OrderCreateReqDTO dto) {
        ProductInfoDTO product = productFeignClient.getProduct(dto.getProductId());

        if (product.getStockQuantity() < dto.getQuantity()) {
            throw new IllegalArgumentException("재고가 부족한 상품입니다: " + product.getName());
        }

        Ordering ordering = new Ordering(memberId, dto.getProductId(), dto.getQuantity());
        orderingRepository.save(ordering);

        stockUpdateProducer.sendStockDecrease(dto.getProductId(), dto.getQuantity());

        return new OrderResDTO(ordering);
    }

    @Transactional(readOnly = true)
    public List<OrderResDTO> myOrders(Long memberId) {
        return orderingRepository.findByMemberIdOrderByCreatedTimeDesc(memberId)
                .stream().map(OrderResDTO::new).toList();
    }

    public void cancel(Long memberId, Long orderId) {
        Ordering ordering = getOwnedOrder(memberId, orderId);
        assertCancelable(ordering);
        ordering.cancel();
        stockUpdateProducer.sendStockRestore(ordering.getProductId(), ordering.getQuantity());
    }

    public void refund(Long memberId, Long orderId) {
        Ordering ordering = getOwnedOrder(memberId, orderId);
        assertCancelable(ordering);
        ordering.refund();
        stockUpdateProducer.sendStockRestore(ordering.getProductId(), ordering.getQuantity());
    }

    private void assertCancelable(Ordering ordering) {
        if (ordering.getOrderStatus() != OrderStatus.ORDERED) {
            throw new IllegalArgumentException("이미 취소되었거나 환불된 주문입니다.");
        }
    }

    private Ordering getOwnedOrder(Long memberId, Long orderId) {
        Ordering ordering = orderingRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 주문입니다."));
        if (!ordering.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 주문만 처리할 수 있습니다.");
        }
        return ordering;
    }
}