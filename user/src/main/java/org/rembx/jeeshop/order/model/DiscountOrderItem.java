package org.rembx.jeeshop.order.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Order item.
 * Represents a stock unit price and its ordered quantity
 */

@Entity
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class DiscountOrderItem extends OrderItem {

    @NotNull
    @Column(name = "discount_id")
    private Long discountId;

    public DiscountOrderItem() {
    }

    public DiscountOrderItem(Long discountId) {
        this.discountId = discountId;
    }


    public Long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Long discountId) {
        this.discountId = discountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DiscountOrderItem that = (DiscountOrderItem) o;

        if (discountId != null ? !discountId.equals(that.discountId) : that.discountId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (discountId != null ? discountId.hashCode() : 0);
        return result;
    }
}
