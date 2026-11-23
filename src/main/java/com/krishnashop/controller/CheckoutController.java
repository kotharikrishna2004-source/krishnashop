package com.krishnashop.controller;

import com.krishnashop.dto.CheckoutDto;
import com.krishnashop.entity.Address;
import com.krishnashop.entity.Cart;
import com.krishnashop.entity.Order;
import com.krishnashop.entity.User;
import com.krishnashop.exception.OutOfStockException;
import com.krishnashop.repository.AddressRepository;
import com.krishnashop.security.CustomUserDetails;
import com.krishnashop.service.CartService;
import com.krishnashop.service.OrderService;
import com.krishnashop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;
    private final AddressRepository addressRepository;

    public CheckoutController(CartService cartService, OrderService orderService, UserService userService, AddressRepository addressRepository) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.userService = userService;
        this.addressRepository = addressRepository;
    }

    @GetMapping("/checkout")
    public String checkoutPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userService.findById(userDetails.getId()).orElseThrow();
        Cart cart = cartService.getOrCreateCartForUser(user);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }

        CheckoutDto checkoutDto = new CheckoutDto();
        checkoutDto.setRecipientName(user.getFullName());
        checkoutDto.setPhoneNumber(user.getPhone() != null ? user.getPhone() : "");

        // Pre-populate default address if available
        List<Address> addresses = addressRepository.findByUser(user);
        if (!addresses.isEmpty()) {
            Address defaultAddr = addresses.stream()
                    .filter(Address::isDefault)
                    .findFirst()
                    .orElse(addresses.get(0));
            checkoutDto.setRecipientName(defaultAddr.getRecipientName());
            checkoutDto.setPhoneNumber(defaultAddr.getPhoneNumber());
            checkoutDto.setStreetAddress(defaultAddr.getStreetAddress());
            checkoutDto.setCity(defaultAddr.getCity());
            checkoutDto.setState(defaultAddr.getState());
            checkoutDto.setPostalCode(defaultAddr.getPostalCode());
        }

        model.addAttribute("checkoutDto", checkoutDto);
        model.addAttribute("cart", cart);

        return "checkout";
    }

    @PostMapping("/checkout/place-order")
    public String placeOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @Valid @ModelAttribute("checkoutDto") CheckoutDto checkoutDto,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        User user = userService.findById(userDetails.getId()).orElseThrow();
        Cart cart = cartService.getOrCreateCartForUser(user);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty.");
            return "redirect:/cart";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("cart", cart);
            return "checkout";
        }

        try {
            Order order = orderService.placeOrder(user, checkoutDto);
            return "redirect:/order-confirmation/" + order.getId();
        } catch (OutOfStockException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/cart";
        } catch (Exception e) {
            model.addAttribute("cart", cart);
            model.addAttribute("errorMessage", "Failed to place order: " + e.getMessage());
            return "checkout";
        }
    }

    @GetMapping("/order-confirmation/{orderId}")
    public String orderConfirmation(@AuthenticationPrincipal CustomUserDetails userDetails,
                                    @PathVariable("orderId") Long orderId,
                                    Model model) {
        Order order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (!order.getUser().getId().equals(userDetails.getId()) && !userDetails.isAdmin()) {
            return "redirect:/orders";
        }

        model.addAttribute("order", order);
        return "order-confirmation";
    }
}
