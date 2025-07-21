package net.electronicstore.common.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import net.electronicstore.common.base.BaseEntity;

@Getter
@Setter
@Entity
public class Basket extends BaseEntity {

  private UUID customerId;

  @OneToMany(mappedBy = "basket", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<BasketItem> items = new ArrayList<>();
}
