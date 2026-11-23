package com.krishnashop.service.impl;

import com.krishnashop.dto.CheckoutDto;
import com.krishnashop.entity.*;
import com.krishnashop.exception.OutOfStockException;
import com.krishnashop.exception.ResourceNotFoundException;
import com.krishnashop.repository.*;
import com.krishnashop.service.CartService;
import com.krishnashop.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final CartService cartService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            CartRepository cartRepository,
                            ProductRepository productRepository,
                            AddressRepository addressRepository,
                            CartService cartService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.addressRepository = addressRepository;
        this.cartService = cartService;
    }

    @Override
    public Order placeOrder(User user, CheckoutDto checkoutDto) {
        Cart cart = cartService.getOrCreateCartForUser(user);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Your cart is empty. Please add products before placing an order.");
        }

        // 1. Verify stock availability for all items in cart first
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new OutOfStockException("Insufficient stock for product: " + product.getName() +
                        ". Available: " + product.getStockQuantity() + ", in cart: " + item.getQuantity());
            }
        }

        // 2. Generate unique Order Number (e.g., KS-20261121-7A3B)
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        String orderNumber = "KS-" + datePart + "-" + randomPart;

        // 3. Save / update address for user if not exists
        Address address = new Address(
                user,
                checkoutDto.getRecipientName(),
                checkoutDto.getPhoneNumber(),
                checkoutDto.getStreetAddress(),
                checkoutDto.getCity(),
                checkoutDto.getState(),
                checkoutDto.getPostalCode()
        );
        address.setDefault(true);
        addressRepository.save(address);

        // 4. Create Order
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(cart.getTotalAmount());
        order.setShippingFee(cart.getShippingFee());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentMethod(checkoutDto.getPaymentMethod() != null ? checkoutDto.getPaymentMethod() : "Cash on Delivery");
        order.setPaymentStatus("Pending");
        order.setRecipientName(checkoutDto.getRecipientName());
        order.setPhoneNumber(checkoutDto.getPhoneNumber());
        order.setDeliveryAddress(checkoutDto.getFormattedAddress());
        order.setNotes(checkoutDto.getNotes());

        Order savedOrder = orderRepository.save(order);

        // 5. Create OrderItems & deduct stock
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();

            OrderItem orderItem = new OrderItem(
                    savedOrder,
                    product,
                    product.getName(),
                    product.getImageUrl(),
                    item.getPrice(),
                    item.getQuantity(),
                    item.getSubtotal()
            );
            savedOrder.getItems().add(orderItem);
            orderItemRepository.save(orderItem);

            // Deduct stock
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        }

        // 6. Clear user cart
        cartService.clearCart(user);

        return savedOrder;
    }

    @Override
    public List<Order> getOrdersForUser(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public Optional<Order> getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByOrderStatusOrderByOrderDateDesc(status);
    }

    @Override
    public List<Order> getRecentOrders() {
        return orderRepository.findTop10ByOrderByOrderDateDesc();
    }

    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        OrderStatus currentStatus = order.getOrderStatus();
        order.setOrderStatus(newStatus);

        // If newly delivered and payment was pending with COD, mark payment as Paid
        if (newStatus == OrderStatus.DELIVERED) {
            order.setPaymentStatus("Paid");
        }

        // If cancelled from an active status, restore stock
        if (newStatus == OrderStatus.CANCELLED && currentStatus != OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                if (item.getProduct() != null) {
                    Product product = item.getProduct();
                    product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                    productRepository.save(product);
                }
            }
        }

        return orderRepository.save(order);
    }

    @Override
    public Order cancelOrder(User user, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You are not authorized to cancel this order.");
        }

        if (order.getOrderStatus() != OrderStatus.PENDING && order.getOrderStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Only Pending or Confirmed orders can be cancelled.");
        }

        return updateOrderStatus(orderId, OrderStatus.CANCELLED);
    }

    @Override
    public Double getTotalSales() {
        Double total = orderRepository.sumTotalSales();
        return total != null ? total : 0.0;
    }

    @Override
    public long countTotalOrders() {
        return orderRepository.count();
    }

    @Override
    public long countOrdersByStatus(OrderStatus status) {
        return orderRepository.countByOrderStatus(status);
    }
}
