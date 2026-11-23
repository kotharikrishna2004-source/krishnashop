package com.krishnashop.controller;

import com.krishnashop.entity.Category;
import com.krishnashop.entity.Product;
import com.krishnashop.service.CategoryService;
import com.krishnashop.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public HomeController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        List<Product> trendingProducts = productService.getTrendingProducts();
        List<Product> bestSellers = productService.getBestSellingProducts();
        List<Product> newArrivals = productService.getNewArrivals();
        List<Product> featuredProducts = productService.getFeaturedProducts();

        model.addAttribute("categories", categories);
        model.addAttribute("trendingProducts", trendingProducts);
        model.addAttribute("bestSellers", bestSellers);
        model.addAttribute("newArrivals", newArrivals);
        model.addAttribute("featuredProducts", featuredProducts);

        return "index";
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "keyword", required = false) String keyword,
                         @RequestParam(value = "categoryId", required = false) Long categoryId,
                         Model model) {
        return "redirect:/products?keyword=" + (keyword != null ? keyword : "") +
                (categoryId != null ? "&categoryId=" + categoryId : "");
    }
}
