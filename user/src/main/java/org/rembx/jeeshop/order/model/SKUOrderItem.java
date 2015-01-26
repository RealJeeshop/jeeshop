package org.rembx.jeeshop.order.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Order item.
 * Represents a stock unit price and its ordered quantity
 */

@Entity
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class SKUOrderItem extends OrderItem {

    @NotNull
    @Column(name = "sku_id")
    private Long skuId;

    @NotNull
    @Column(nullable = false)
    private Integer quantity;

    public SKUOrderItem() {
    }

    public SKUOrderItem(Long skuId, Integer quantity) {
        this.skuId = skuId;
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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
        if (!super.equals(o)) return false;

        SKUOrderItem that = (SKUOrderItem) o;

        if (quantity != null ? !quantity.equals(that.quantity) : that.quantity != null) return false;
        if (skuId != null ? !skuId.equals(that.skuId) : that.skuId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (skuId != null ? skuId.hashCode() : 0);
        result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
        return result;
    }


}
