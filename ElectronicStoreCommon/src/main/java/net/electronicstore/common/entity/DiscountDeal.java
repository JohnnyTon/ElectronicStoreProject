package net.electronicstore.common.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;
import net.electronicstore.common.base.BaseEntity;
import net.electronicstore.common.enums.DiscountType;

@Getter
@Setter
@Entity
public class DiscountDeal extends BaseEntity {

  @ManyToMany
  @JoinTable(
      name = "discount_deal_product",
      joinColumns = @JoinColumn(name = "discount_deal_id"),
      inverseJoinColumns = @JoinColumn(name = "product_id")
  )
  private Set<Product> products = new HashSet<>();

  @Enumerated(EnumType.STRING)
  private DiscountType type;

  private BigDecimal discountPercent;

  private Integer buyQuantity;

  private Integer getQuantity;

  private LocalDateTime expiresAt;
}
