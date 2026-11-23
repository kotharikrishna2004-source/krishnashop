package com.krishnashop.service.impl;

import com.krishnashop.dto.ProductDto;
import com.krishnashop.entity.Category;
import com.krishnashop.entity.Product;
import com.krishnashop.exception.ResourceNotFoundException;
import com.krishnashop.repository.CategoryRepository;
import com.krishnashop.repository.ProductRepository;
import com.krishnashop.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product createOrUpdateProductFromDto(ProductDto dto) {
        Product product;
        if (dto.getId() != null) {
            product = productRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + dto.getId()));
        } else {
            product = new Product();
        }

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setDiscountPrice(dto.getDiscountPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setImageUrl(dto.getImageUrl());
        product.setFeatured(dto.isFeatured());
        product.setTrending(dto.isTrending());
        product.setBestSeller(dto.isBestSeller());
        product.setNewArrival(dto.isNewArrival());

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }

        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Product> getTrendingProducts() {
        return productRepository.findTop8ByIsTrendingTrue();
    }

    @Override
    public List<Product> getBestSellingProducts() {
        return productRepository.findTop8ByIsBestSellerTrue();
    }

    @Override
    public List<Product> getNewArrivals() {
        return productRepository.findTop8ByIsNewArrivalTrue();
    }

    @Override
    public List<Product> getFeaturedProducts() {
        return productRepository.findByIsFeaturedTrue();
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productRepository.findAll();
        }
        return productRepository.searchByKeyword(keyword.trim());
    }

    @Override
    public Page<Product> filterAndSortProducts(Long categoryId, String keyword, Double minPrice, Double maxPrice, boolean inStockOnly, String sortBy, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if (sortBy != null) {
            switch (sortBy.toLowerCase()) {
                case "price_asc" -> sort = Sort.by(Sort.Direction.ASC, "price");
                case "price_desc" -> sort = Sort.by(Sort.Direction.DESC, "price");
                case "name_asc" -> sort = Sort.by(Sort.Direction.ASC, "name");
                case "rating" -> sort = Sort.by(Sort.Direction.DESC, "rating");
                case "newest" -> sort = Sort.by(Sort.Direction.DESC, "createdAt");
                default -> sort = Sort.by(Sort.Direction.DESC, "createdAt");
            }
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        String cleanedKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        return productRepository.filterProducts(categoryId, cleanedKeyword, minPrice, maxPrice, inStockOnly, pageable);
    }

    @Override
    public long countProducts() {
        return productRepository.count();
    }

    @Override
    public long countLowStockProducts(int threshold) {
        return productRepository.countByStockQuantityLessThanEqual(threshold);
    }
}
