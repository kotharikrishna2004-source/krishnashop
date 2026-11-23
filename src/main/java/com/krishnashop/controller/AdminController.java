package com.krishnashop.controller;

import com.krishnashop.dto.ProductDto;
import com.krishnashop.entity.Category;
import com.krishnashop.entity.Order;
import com.krishnashop.entity.OrderStatus;
import com.krishnashop.entity.Product;
import com.krishnashop.entity.User;
import com.krishnashop.exception.ResourceNotFoundException;
import com.krishnashop.service.CategoryService;
import com.krishnashop.service.OrderService;
import com.krishnashop.service.ProductService;
import com.krishnashop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final UserService userService;

    public AdminController(ProductService productService,
                           CategoryService categoryService,
                           OrderService orderService,
                           UserService userService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.orderService = orderService;
        this.userService = userService;
    }

    // 1. Dashboard
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        Double totalSales = orderService.getTotalSales();
        long totalOrders = orderService.countTotalOrders();
        long totalProducts = productService.countProducts();
        long totalCustomers = userService.countCustomers();
        long pendingOrders = orderService.countOrdersByStatus(OrderStatus.PENDING);
        long lowStockCount = productService.countLowStockProducts(5);
        List<Order> recentOrders = orderService.getRecentOrders();

        model.addAttribute("totalSales", totalSales);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("lowStockCount", lowStockCount);
        model.addAttribute("recentOrders", recentOrders);

        return "admin/dashboard";
    }

    // 2. Product Management
    @GetMapping("/products")
    public String listProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "admin/products";
    }

    @GetMapping("/products/new")
    public String newProductForm(Model model) {
        model.addAttribute("productDto", new ProductDto());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("isEdit", false);
        return "admin/product-form";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setDiscountPrice(product.getDiscountPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setImageUrl(product.getImageUrl());
        dto.setFeatured(product.isFeatured());
        dto.setTrending(product.isTrending());
        dto.setBestSeller(product.isBestSeller());
        dto.setNewArrival(product.isNewArrival());
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
        }

        model.addAttribute("productDto", dto);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("isEdit", true);
        return "admin/product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@Valid @ModelAttribute("productDto") ProductDto productDto,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("isEdit", productDto.getId() != null);
            return "admin/product-form";
        }

        try {
            productService.createOrUpdateProductFromDto(productDto);
            redirectAttributes.addFlashAttribute("successMessage", "Product saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving product: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete product: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    // 3. Category Management
    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/categories";
    }

    @GetMapping("/categories/new")
    public String newCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("isEdit", false);
        return "admin/category-form";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategoryForm(@PathVariable("id") Long id, Model model) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        model.addAttribute("category", category);
        model.addAttribute("isEdit", true);
        return "admin/category-form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(@ModelAttribute("category") Category category,
                               RedirectAttributes redirectAttributes) {
        try {
            categoryService.saveCategory(category);
            redirectAttributes.addFlashAttribute("successMessage", "Category saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving category: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete category: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    // 4. Order Management
    @GetMapping("/orders")
    public String listOrders(@RequestParam(value = "status", required = false) OrderStatus status, Model model) {
        List<Order> orders;
        if (status != null) {
            orders = orderService.getOrdersByStatus(status);
        } else {
            orders = orderService.getAllOrders();
        }
        model.addAttribute("orders", orders);
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("selectedStatus", status);
        return "admin/orders";
    }

    @GetMapping("/orders/{id}")
    public String viewOrder(@PathVariable("id") Long id, Model model) {
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        model.addAttribute("order", order);
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/order-details";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable("id") Long id,
                                    @RequestParam("status") OrderStatus status,
                                    RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Order status updated to " + status.getDisplayName());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update order status: " + e.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }

    // 5. Customer Management
    @GetMapping("/customers")
    public String listCustomers(Model model) {
        List<User> customers = userService.getAllCustomers();
        model.addAttribute("customers", customers);
        return "admin/customers";
    }
}
