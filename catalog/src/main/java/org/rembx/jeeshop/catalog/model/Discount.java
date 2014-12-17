package org.rembx.jeeshop.catalog.model;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Discount entity
 */
@Entity
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class Discount extends CatalogItem {

    public static enum Type {
        DISCOUNT_RATE,
        ORDER_DISCOUNT_AMOUNT,
        SHIPPING_FEE_DISCOUNT_AMOUNT
    }

    public static enum Trigger {
        QUANTITY,
        AMOUNT,
    }

    @Size(max = 100)
    @Column(length = 100)
    private String voucherCode;

    private Integer usesPerCustomer;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Type type;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Trigger triggerRule;

    private Double triggerValue;

    private Double discountValue;

    @ManyToMany(mappedBy = "discounts")
    @XmlTransient
    private List<SKU> skus;

    @ManyToMany(mappedBy = "discounts")
    @XmlTransient
    private List<Product> products;

    /**
     * Cannot be used with other discounts when true
     */
    private Boolean uniqueUse;

    public Discount() {
    }

    public Discount(Long id) {
        this.id = id;
    }

    public Discount(String name, String description, Type type, Trigger triggerRule, String voucherCode, Double discountValue,Double triggerValue, Integer usesPerCustomer, Boolean uniqueUse, Date startDate, Date endDate, Boolean disabled) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.triggerRule = triggerRule;
        this.voucherCode = voucherCode;
        this.discountValue = discountValue;
        this.triggerValue = triggerValue;
        this.usesPerCustomer = usesPerCustomer;
        this.uniqueUse = uniqueUse;
        this.startDate = startDate;
        this.endDate = endDate;
        this.disabled = disabled;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Trigger getTrigger() {
        return triggerRule;
    }

    public void setTrigger(Trigger triggerRule) {
        this.triggerRule = triggerRule;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public List<SKU> getSkus() {
        return skus;
    }

    public void setSkus(List<SKU> skus) {
        this.skus = skus;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Integer getUsesPerCustomer() {
        return usesPerCustomer;
    }

    public void setUsesPerCustomer(Integer usesPerCustomer) {
        this.usesPerCustomer = usesPerCustomer;
    }

    public Boolean getUniqueUse() {
        return uniqueUse;
    }

    public void setUniqueUse(Boolean uniqueUse) {
        this.uniqueUse = uniqueUse;
    }

    public Trigger getTriggerRule() {
        return triggerRule;
    }

    public void setTriggerRule(Trigger triggerRule) {
        this.triggerRule = triggerRule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Discount discount = (Discount) o;

        if (discountValue != null ? !discountValue.equals(discount.discountValue) : discount.discountValue != null)
            return false;
        if (triggerRule != discount.triggerRule) return false;
        if (triggerValue != null ? !triggerValue.equals(discount.triggerValue) : discount.triggerValue != null)
            return false;
        if (type != discount.type) return false;
        if (uniqueUse != null ? !uniqueUse.equals(discount.uniqueUse) : discount.uniqueUse != null) return false;
        if (usesPerCustomer != null ? !usesPerCustomer.equals(discount.usesPerCustomer) : discount.usesPerCustomer != null)
            return false;
        if (voucherCode != null ? !voucherCode.equals(discount.voucherCode) : discount.voucherCode != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (voucherCode != null ? voucherCode.hashCode() : 0);
        result = 31 * result + (usesPerCustomer != null ? usesPerCustomer.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (triggerRule != null ? triggerRule.hashCode() : 0);
        result = 31 * result + (triggerValue != null ? triggerValue.hashCode() : 0);
        result = 31 * result + (discountValue != null ? discountValue.hashCode() : 0);
        result = 31 * result + (uniqueUse != null ? uniqueUse.hashCode() : 0);
        return result;
    }
}
