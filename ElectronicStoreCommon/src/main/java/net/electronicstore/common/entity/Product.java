package net.electronicstore.common.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;
import net.electronicstore.common.base.BaseEntity;

@Getter
@Setter
@Entity
public class Product extends BaseEntity {

  private String name;

  private String description;

  private BigDecimal price;

  private String category;

  private boolean available;

  private int stock;

  @ManyToMany(mappedBy = "products")
  private Set<DiscountDeal> discountDeals = new HashSet<>();
}
