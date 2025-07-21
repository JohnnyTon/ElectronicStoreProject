package net.electronicstore.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import net.electronicstore.common.entity.Receipt;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
}
