package com.krishnashop.service;

import com.krishnashop.dto.ProductDto;
import com.krishnashop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> getAllProducts();

    Optional<Product> getProductById(Long id);

    Product saveProduct(Product product);

    Product createOrUpdateProductFromDto(ProductDto productDto);

    void deleteProduct(Long id);

    List<Product> getProductsByCategory(Long categoryId);

    List<Product> getTrendingProducts();

    List<Product> getBestSellingProducts();

    List<Product> getNewArrivals();

    List<Product> getFeaturedProducts();

    List<Product> searchProducts(String keyword);

    Page<Product> filterAndSortProducts(Long categoryId, String keyword, Double minPrice, Double maxPrice, boolean inStockOnly, String sortBy, int page, int size);

    long countProducts();

    long countLowStockProducts(int threshold);
}
