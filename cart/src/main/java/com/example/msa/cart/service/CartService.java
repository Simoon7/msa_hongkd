// cart/service/CartService.java
package com.example.msa.cart.service;

import com.example.msa.cart.client.ProductFeignClient;
import com.example.msa.cart.domain.Cart;
import com.example.msa.cart.domain.CartItem;
import com.example.msa.cart.dto.*;
import com.example.msa.cart.repository.CartItemRepository;
import com.example.msa.cart.repository.CartRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductFeignClient productFeignClient;

    public Cart getOrCreateCart(Long memberId) {
        return cartRepository.findByMemberId(memberId)
                .orElseGet(() -> cartRepository.save(new Cart(memberId)));
    }

    public void addItem(Long memberId, CartItemAddReqDTO dto) {
        // 상품이 실제 존재하는지 Feign으로 확인 (없으면 예외)
        ProductInfoDTO product = productFeignClient.getProduct(dto.getProductId());

        Cart cart = getOrCreateCart(memberId);

        cartItemRepository.findByCartAndProductId(cart, product.getId()).ifPresentOrElse(
                existing -> existing.changeQuantity(existing.getQuantity() + dto.getQuantity()),
                () -> cart.addItem(cartItemRepository.save(new CartItem(cart, product.getId(), dto.getQuantity())))
        );
    }

    public void updateItemQuantity(Long memberId, Long cartItemId, CartItemUpdateReqDTO dto) {
        getOwnedCartItem(memberId, cartItemId).changeQuantity(dto.getQuantity());
    }

    public void removeItem(Long memberId, Long cartItemId) {
        CartItem item = getOwnedCartItem(memberId, cartItemId);
        item.getCart().removeItem(item);
        cartItemRepository.delete(item);
    }

    public CartResDTO getCart(Long memberId) {
        Cart cart = getOrCreateCart(memberId);

        List<CartItemResDTO> items = cart.getCartItems().stream()
                .map(item -> {
                    ProductInfoDTO product = productFeignClient.getProduct(item.getProductId());
                    return new CartItemResDTO(item, product);
                })
                .toList();

        return new CartResDTO(cart.getId(), items);
    }

    private CartItem getOwnedCartItem(Long memberId, Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 장바구니 항목입니다."));
        if (!item.getCart().getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 장바구니만 수정할 수 있습니다.");
        }
        return item;
    }

    public void clearCart(Long memberId) {
        Cart cart = getOrCreateCart(memberId);
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
    }
}