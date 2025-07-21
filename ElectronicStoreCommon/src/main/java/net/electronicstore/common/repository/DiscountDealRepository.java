package net.electronicstore.common.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import net.electronicstore.common.entity.DiscountDeal;

@Repository
public interface DiscountDealRepository extends JpaRepository<DiscountDeal, Long> {
  @Query("SELECT d FROM DiscountDeal d JOIN d.products p WHERE p.id = :productId AND d.expiresAt > :now")
  List<DiscountDeal> findActiveDealsByProductId(@Param("productId") Long productId,
      @Param("now") LocalDateTime now);

}
