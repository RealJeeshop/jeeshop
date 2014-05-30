package org.rembx.jeeshop.catalog.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;
import java.util.Set;

/**
 * Created by remi on 20/05/14.
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
        QUANTITY_OF_PRODUCTS,
        QUANTITY_OF_CATEGORY,
        ORDER_AMOUNT
    }

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private Trigger triggerRule;

    @XmlTransient
    private String voucherCode;

    private Double amount;

    @ManyToMany(mappedBy = "discounts")
    @XmlTransient
    private Set<SKU> skus;

    @ManyToMany(mappedBy = "discounts")
    @XmlTransient
    private Set<Product> products;

    @XmlTransient
    private Integer usesPerCustomer;

    /**
     * Cannot be used with other discounts when true
     */
    @XmlTransient
    private Boolean uniqueUse;

    public Discount() {
    }

    public Discount(String name, String description, Type type, Trigger triggerRule, String voucherCode, Double amount, Integer usesPerCustomer, Boolean uniqueUse, Date startDate, Date endDate, Boolean disabled) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.triggerRule = triggerRule;
        this.voucherCode = voucherCode;
        this.amount = amount;
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


    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Set<SKU> getSkus() {
        return skus;
    }

    public void setSkus(Set<SKU> skus) {
        this.skus = skus;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
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

        if (amount != null ? !amount.equals(discount.amount) : discount.amount != null) return false;
        if (triggerRule != discount.triggerRule) return false;
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
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (triggerRule != null ? triggerRule.hashCode() : 0);
        result = 31 * result + (voucherCode != null ? voucherCode.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (usesPerCustomer != null ? usesPerCustomer.hashCode() : 0);
        result = 31 * result + (uniqueUse != null ? uniqueUse.hashCode() : 0);
        return result;
    }
}
