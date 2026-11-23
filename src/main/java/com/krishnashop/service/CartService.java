package com.krishnashop.service;

import com.krishnashop.entity.Cart;
import com.krishnashop.entity.User;

public interface CartService {

    Cart getOrCreateCartForUser(User user);

    Cart addToCart(User user, Long productId, int quantity);

    Cart updateItemQuantity(User user, Long cartItemId, int quantity);

    Cart removeItemFromCart(User user, Long cartItemId);

    void clearCart(User user);

    int getCartItemCount(User user);
}
