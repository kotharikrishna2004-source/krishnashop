package com.krishnashop.repository;

import com.krishnashop.entity.Order;
import com.krishnashop.entity.OrderStatus;
import com.krishnashop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByOrderDateDesc(User user);

    List<Order> findAllByOrderByOrderDateDesc();

    List<Order> findByOrderStatusOrderByOrderDateDesc(OrderStatus status);

    List<Order> findTop10ByOrderByOrderDateDesc();

    Optional<Order> findByOrderNumber(String orderNumber);

    long countByOrderStatus(OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0.0) FROM Order o WHERE o.orderStatus <> com.krishnashop.entity.OrderStatus.CANCELLED")
    Double sumTotalSales();
}
