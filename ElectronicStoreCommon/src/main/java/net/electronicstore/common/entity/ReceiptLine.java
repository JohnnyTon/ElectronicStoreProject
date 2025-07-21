package net.electronicstore.common.entity;

import java.math.BigDecimal;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import net.electronicstore.common.base.BaseEntity;

@Getter
@Setter
@Entity
public class ReceiptLine extends BaseEntity {

    @ManyToOne
    private Receipt receipt;

    @ManyToOne
    private Product product;

    private int quantity;

    private BigDecimal unitPrice;

    private BigDecimal subtotal;

    private String appliedDeal;
}

