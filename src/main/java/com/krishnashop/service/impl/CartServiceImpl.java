package com.krishnashop.service.impl;

import com.krishnashop.entity.Cart;
import com.krishnashop.entity.CartItem;
import com.krishnashop.entity.Product;
import com.krishnashop.entity.User;
import com.krishnashop.exception.OutOfStockException;
import com.krishnashop.exception.ResourceNotFoundException;
import com.krishnashop.repository.CartItemRepository;
import com.krishnashop.repository.CartRepository;
import com.krishnashop.repository.ProductRepository;
import com.krishnashop.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Cart getOrCreateCartForUser(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart(user);
                    return cartRepository.save(newCart);
                });
    }

    @Override
    public Cart addToCart(User user, Long productId, int quantity) {
        if (quantity <= 0) {
            quantity = 1;
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (!product.isInStock()) {
            throw new OutOfStockException("Sorry, '" + product.getName() + "' is currently out of stock.");
        }

        Cart cart = getOrCreateCartForUser(user);

        // Check if item already exists in cart
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + quantity;
            if (newQuantity > product.getStockQuantity()) {
                throw new OutOfStockException("Cannot add " + quantity + " more. Only " + product.getStockQuantity() + " items available in stock.");
            }
            existingItem.setQuantity(newQuantity);
            existingItem.setPrice(product.getPrice());
            cartItemRepository.save(existingItem);
        } else {
            if (quantity > product.getStockQuantity()) {
                throw new OutOfStockException("Cannot add " + quantity + " items. Only " + product.getStockQuantity() + " available in stock.");
            }
            CartItem newItem = new CartItem(cart, product, quantity, product.getPrice());
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    @Override
    public Cart updateItemQuantity(User user, Long cartItemId, int quantity) {
        Cart cart = getOrCreateCartForUser(user);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to the user's cart.");
        }

        if (quantity <= 0) {
            // Remove item
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            Product product = item.getProduct();
            if (quantity > product.getStockQuantity()) {
                throw new OutOfStockException("Cannot set quantity to " + quantity + ". Only " + product.getStockQuantity() + " items in stock.");
            }
            item.setQuantity(quantity);
            item.setPrice(product.getPrice());
            cartItemRepository.save(item);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    @Override
    public Cart removeItemFromCart(User user, Long cartItemId) {
        Cart cart = getOrCreateCartForUser(user);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to the user's cart.");
        }

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    @Override
    public void clearCart(User user) {
        Cart cart = getOrCreateCartForUser(user);
        cart.getItems().clear();
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Override
    public int getCartItemCount(User user) {
        Cart cart = getOrCreateCartForUser(user);
        return cart.getTotalItemCount();
    }
}
