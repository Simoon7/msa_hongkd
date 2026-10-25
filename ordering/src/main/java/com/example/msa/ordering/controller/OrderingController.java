package com.example.msa.ordering.controller;

import com.example.msa.ordering.dto.OrderCreateReqDTO;
import com.example.msa.ordering.dto.OrderResDTO;
import com.example.msa.ordering.service.OrderingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ordering")
public class OrderingController {

    private final OrderingService orderingService;

    @PostMapping("/create")
    public ResponseEntity<OrderResDTO> create(@RequestHeader("X-User-Id") Long memberId,
                                              @RequestBody OrderCreateReqDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderingService.createOrder(memberId, dto));
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderResDTO>> myOrders(@RequestHeader("X-User-Id") Long memberId) {
        return ResponseEntity.ok(orderingService.myOrders(memberId));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancel(@RequestHeader("X-User-Id") Long memberId, @PathVariable Long orderId) {
        orderingService.cancel(memberId, orderId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{orderId}/refund")
    public ResponseEntity<Void> refund(@RequestHeader("X-User-Id") Long memberId, @PathVariable Long orderId) {
        orderingService.refund(memberId, orderId);
        return ResponseEntity.ok().build();
    }
}