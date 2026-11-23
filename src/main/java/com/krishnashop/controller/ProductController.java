package com.krishnashop.controller;

import com.krishnashop.entity.Category;
import com.krishnashop.entity.Product;
import com.krishnashop.exception.ResourceNotFoundException;
import com.krishnashop.service.CategoryService;
import com.krishnashop.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/products")
    public String listProducts(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @RequestParam(value = "inStockOnly", required = false, defaultValue = "false") boolean inStockOnly,
            @RequestParam(value = "sortBy", required = false, defaultValue = "newest") String sortBy,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "12") int size,
            Model model) {

        Page<Product> productPage = productService.filterAndSortProducts(
                categoryId, keyword, minPrice, maxPrice, inStockOnly, sortBy, page, size);

        Category selectedCategory = null;
        if (categoryId != null) {
            selectedCategory = categoryService.getCategoryById(categoryId).orElse(null);
        }

        model.addAttribute("productPage", productPage);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedCategory", selectedCategory);
        model.addAttribute("keyword", keyword);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("inStockOnly", inStockOnly);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalElements", productPage.getTotalElements());

        return "products";
    }

    @GetMapping("/product/{id}")
    public String productDetails(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        List<Product> relatedProducts = List.of();
        if (product.getCategory() != null) {
            relatedProducts = productService.getProductsByCategory(product.getCategory().getId()).stream()
                    .filter(p -> !p.getId().equals(product.getId()))
                    .limit(4)
                    .toList();
        }

        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", relatedProducts);

        return "product-details";
    }

    @GetMapping("/category/{id}")
    public String categoryProducts(@PathVariable("id") Long id) {
        return "redirect:/products?categoryId=" + id;
    }
}
