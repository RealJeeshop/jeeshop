package org.rembx.jeeshop.order.model;

import org.rembx.jeeshop.catalog.model.SKU;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Order item.
 * Represents a stock unit price and its ordered quantity
 */

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @XmlTransient
    private SKU sku;

    @Transient
    @NotNull
    private Long skuId;

    @NotNull
    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(optional = false)
    private Order order;

    public OrderItem() {
    }

    public OrderItem(Long skuId, Integer quantity) {
        this.skuId = skuId;
        this.quantity = quantity;
    }

    public OrderItem(SKU sku, Integer quantity) {
        this.sku = sku;
        this.quantity = quantity;
    }

    @PostLoad
    public void postLoad() {
        if (sku != null)
            skuId = sku.getId();
    }

    public SKU getSku() {
        return sku;
    }

    public void setSku(SKU sku) {
        this.sku = sku;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderItem orderItem = (OrderItem) o;

        if (id != null ? !id.equals(orderItem.id) : orderItem.id != null) return false;
        if (quantity != null ? !quantity.equals(orderItem.quantity) : orderItem.quantity != null) return false;
        if (skuId != null ? !skuId.equals(orderItem.skuId) : orderItem.skuId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (skuId != null ? skuId.hashCode() : 0);
        result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", skuId=" + skuId +
                ", quantity=" + quantity +
                '}';
    }
}
