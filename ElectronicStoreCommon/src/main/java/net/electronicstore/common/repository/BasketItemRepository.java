package net.electronicstore.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import net.electronicstore.common.entity.BasketItem;

@Repository
public interface BasketItemRepository extends JpaRepository<BasketItem, Long> {
}
