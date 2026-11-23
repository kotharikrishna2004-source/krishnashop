package com.krishnashop.controller;

import com.krishnashop.entity.Cart;
import com.krishnashop.entity.User;
import com.krishnashop.exception.OutOfStockException;
import com.krishnashop.security.CustomUserDetails;
import com.krishnashop.service.CartService;
import com.krishnashop.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping
    public String viewCart(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userService.findById(userDetails.getId()).orElseThrow();
        Cart cart = cartService.getOrCreateCartForUser(user);
        model.addAttribute("cart", cart);
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@AuthenticationPrincipal CustomUserDetails userDetails,
                            @RequestParam("productId") Long productId,
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                            @RequestParam(value = "buyNow", defaultValue = "false") boolean buyNow,
                            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(userDetails.getId()).orElseThrow();
            cartService.addToCart(user, productId, quantity);
            if (buyNow) {
                return "redirect:/checkout";
            }
            redirectAttributes.addFlashAttribute("successMessage", "Product added to your cart!");
        } catch (OutOfStockException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/product/" + productId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding product to cart: " + e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @RequestParam("itemId") Long itemId,
                                 @RequestParam("quantity") int quantity,
                                 RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(userDetails.getId()).orElseThrow();
            cartService.updateItemQuantity(user, itemId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Cart updated successfully.");
        } catch (OutOfStockException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update quantity: " + e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove/{itemId}")
    public String removeItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @PathVariable("itemId") Long itemId,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(userDetails.getId()).orElseThrow();
            cartService.removeItemFromCart(user, itemId);
            redirectAttributes.addFlashAttribute("successMessage", "Item removed from cart.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to remove item: " + e.getMessage());
        }
        return "redirect:/cart";
    }
}
