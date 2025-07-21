package net.electronicstore.common.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import net.electronicstore.common.base.BaseEntity;

@Getter
@Setter
@Entity
public class BasketItem extends BaseEntity {

  @ManyToOne
  private Basket basket;

  @ManyToOne
  private Product product;

  private int quantity;
}
