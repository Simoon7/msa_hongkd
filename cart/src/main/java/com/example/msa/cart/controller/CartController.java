// cart/controller/CartController.java
package com.example.msa.cart.controller;

import com.example.msa.cart.dto.*;
import com.example.msa.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResDTO> getCart(@RequestHeader("X-User-Id") Long memberId) {
        return ResponseEntity.ok(cartService.getCart(memberId));
    }

    @PostMapping("/items")
    public ResponseEntity<Void> addItem(@RequestHeader("X-User-Id") Long memberId,
                                        @RequestBody CartItemAddReqDTO dto) {
        cartService.addItem(memberId, dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<Void> updateItem(@RequestHeader("X-User-Id") Long memberId,
                                           @PathVariable Long cartItemId,
                                           @RequestBody CartItemUpdateReqDTO dto) {
        cartService.updateItemQuantity(memberId, cartItemId, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> removeItem(@RequestHeader("X-User-Id") Long memberId,
                                           @PathVariable Long cartItemId) {
        cartService.removeItem(memberId, cartItemId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@RequestHeader("X-User-Id") Long memberId) {
        cartService.clearCart(memberId);
        return ResponseEntity.ok().build();
    }
}