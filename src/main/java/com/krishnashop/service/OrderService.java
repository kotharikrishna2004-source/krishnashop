package com.krishnashop.service;

import com.krishnashop.dto.CheckoutDto;
import com.krishnashop.entity.Order;
import com.krishnashop.entity.OrderStatus;
import com.krishnashop.entity.User;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    Order placeOrder(User user, CheckoutDto checkoutDto);

    List<Order> getOrdersForUser(User user);

    Optional<Order> getOrderById(Long id);

    Optional<Order> getOrderByOrderNumber(String orderNumber);

    List<Order> getAllOrders();

    List<Order> getOrdersByStatus(OrderStatus status);

    List<Order> getRecentOrders();

    Order updateOrderStatus(Long orderId, OrderStatus newStatus);

    Order cancelOrder(User user, Long orderId);

    Double getTotalSales();

    long countTotalOrders();

    long countOrdersByStatus(OrderStatus status);
}
