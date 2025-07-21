package net.electronicstore.common.repository;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import net.electronicstore.common.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

  List<Product> findByAvailableTrue();

  List<Product> findByCategory(String category);

  List<Product> findByPriceBetweenAndAvailableTrue(BigDecimal minPrice, BigDecimal maxPrice);

  boolean existsByName(String name);
}
