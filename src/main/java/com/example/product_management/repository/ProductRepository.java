package com.example.product_management.repository;

import com.example.product_management.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Basic methods
    List<Product> findByCategory(String category);
    boolean existsByProductCode(String productCode);
    
    // Pagination for Quick Search
    Page<Product> findByNameContaining(String keyword, Pageable pageable);
    
    // Pagination for Advanced Search (Updated return type to Page)
    @Query("SELECT p FROM Product p WHERE " +
           "(:name IS NULL OR p.name LIKE %:name%) AND " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> searchProducts(@Param("name") String name,
                                 @Param("category") String category,
                                 @Param("minPrice") BigDecimal minPrice,
                                 @Param("maxPrice") BigDecimal maxPrice,
                                 Pageable pageable);

    @Query("SELECT DISTINCT p.category FROM Product p ORDER BY p.category")
    List<String> findAllCategories();

    // Statistics
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category = :category")
    long countByCategory(@Param("category") String category);

    @Query("SELECT COALESCE(SUM(p.price * p.quantity), 0) FROM Product p")
    BigDecimal calculateTotalValue();

    @Query("SELECT COALESCE(AVG(p.price), 0.0) FROM Product p")
    BigDecimal calculateAveragePrice();

    @Query("SELECT p FROM Product p WHERE p.quantity < :threshold")
    List<Product> findLowStockProducts(@Param("threshold") int threshold);

    List<Product> findTop5ByOrderByCreatedAtDesc();
}