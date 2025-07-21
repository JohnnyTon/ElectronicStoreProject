package net.electronicstore.common.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import net.electronicstore.common.entity.Basket;

@Repository
public interface BasketRepository extends JpaRepository<Basket, Long> {
  Optional<Basket> findByCustomerId(UUID customerId);
}
