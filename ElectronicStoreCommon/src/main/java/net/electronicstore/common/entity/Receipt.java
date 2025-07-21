package net.electronicstore.common.entity;

import java.math.BigDecimal;
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
public class Receipt extends BaseEntity {

    private UUID customerId;

    private BigDecimal total;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceiptLine> lines = new ArrayList<>();
}

