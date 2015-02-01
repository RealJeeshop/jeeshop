package org.rembx.jeeshop.order.model;

import org.rembx.jeeshop.user.model.Address;
import org.rembx.jeeshop.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Date;
import java.util.List;

/**
 * OrderDiscount entity
 */

@Embeddable
public class OrderDiscount {

    public OrderDiscount() {
    }

    public OrderDiscount(Long discountId, Double discountValue) {
        this.discountId = discountId;
        this.discountValue = discountValue;
    }

    @NotNull
    @Column(name = "discount_id")
    private Long discountId;

    private Double discountValue;

    @Transient
    private String displayName;
    @Transient
    private URI presentationImageURI;
    @Transient
    private Boolean rateType;

    public Long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Long discountId) {
        this.discountId = discountId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public URI getPresentationImageURI() {
        return presentationImageURI;
    }

    public void setPresentationImageURI(URI presentationImageURI) {
        this.presentationImageURI = presentationImageURI;
    }

    public Double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(Double discountValue) {
        this.discountValue = discountValue;
    }

    public Boolean getRateType() {
        return rateType;
    }

    public void setRateType(Boolean rateType) {
        this.rateType = rateType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderDiscount that = (OrderDiscount) o;

        if (discountId != null ? !discountId.equals(that.discountId) : that.discountId != null) return false;
        if (discountValue != null ? !discountValue.equals(that.discountValue) : that.discountValue != null)
            return false;
        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) return false;
        if (presentationImageURI != null ? !presentationImageURI.equals(that.presentationImageURI) : that.presentationImageURI != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = discountId != null ? discountId.hashCode() : 0;
        result = 31 * result + (discountValue != null ? discountValue.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (presentationImageURI != null ? presentationImageURI.hashCode() : 0);
        return result;
    }
}
