package com.krishnashop.controller;

import com.krishnashop.entity.Order;
import com.krishnashop.entity.User;
import com.krishnashop.security.CustomUserDetails;
import com.krishnashop.service.OrderService;
import com.krishnashop.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping
    public String myOrders(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userService.findById(userDetails.getId()).orElseThrow();
        List<Order> orders = orderService.getOrdersForUser(user);
        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/{id}")
    public String orderDetails(@AuthenticationPrincipal CustomUserDetails userDetails,
                               @PathVariable("id") Long id,
                               Model model) {
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));

        // Ensure user can view only their own orders
        if (!order.getUser().getId().equals(userDetails.getId()) && !userDetails.isAdmin()) {
            return "redirect:/orders";
        }

        model.addAttribute("order", order);
        return "order-details";
    }

    @PostMapping("/{id}/cancel")
    public String cancelOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @PathVariable("id") Long id,
                              RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(userDetails.getId()).orElseThrow();
            orderService.cancelOrder(user, id);
            redirectAttributes.addFlashAttribute("successMessage", "Order has been cancelled successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/orders/" + id;
    }
}
