package org.rembx.jeeshop.catalog.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by remi on 20/05/14.
 */
@Entity
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class Discount extends CatalogItem{

    public static enum Type {
        DISCOUNT_RATE,
        ORDER_DISCOUNT_AMOUNT,
        SHIPPING_FEE_DISCOUNT_AMOUNT
    }

    public static enum Trigger{
        QUANTITY_OF_PRODUCTS,
        QUANTITY_OF_CATEGORY,
        ORDER_AMOUNT
    }

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private Trigger triggerRule;

    private String voucherCode;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "discountId"),
            inverseJoinColumns = @JoinColumn(name = "presentationId"))
    private Set<Presentation> presentation;

    private Double amount;

    private Double discount;

    @ManyToMany
    @OrderColumn(name="orderIdx")
    @JoinTable(joinColumns = @JoinColumn(name = "discountId"),
            inverseJoinColumns = @JoinColumn(name = "skuId"))
    @XmlTransient
    private List<SKU> skus;

    @ManyToMany
    @OrderColumn(name="orderIdx")
    @JoinTable(joinColumns = @JoinColumn(name = "discountId"),
            inverseJoinColumns = @JoinColumn(name = "productId"))
    @XmlTransient
    private List<Product> products;

    private Integer usesPerCustomer;

    /**
     * Cannot be used with other discounts when true
     */
    private Boolean uniqueUse;

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

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
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

    public Set<Presentation> getPresentation() {
        return presentation;
    }

    public void setPresentation(Set<Presentation> presentation) {
        this.presentation = presentation;
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

        Discount discount1 = (Discount) o;

        if (amount != null ? !amount.equals(discount1.amount) : discount1.amount != null) return false;
        if (discount != null ? !discount.equals(discount1.discount) : discount1.discount != null) return false;
        if (triggerRule != discount1.triggerRule) return false;
        if (type != discount1.type) return false;
        if (uniqueUse != null ? !uniqueUse.equals(discount1.uniqueUse) : discount1.uniqueUse != null) return false;
        if (usesPerCustomer != null ? !usesPerCustomer.equals(discount1.usesPerCustomer) : discount1.usesPerCustomer != null)
            return false;
        if (voucherCode != null ? !voucherCode.equals(discount1.voucherCode) : discount1.voucherCode != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (triggerRule != null ? triggerRule.hashCode() : 0);
        result = 31 * result + (voucherCode != null ? voucherCode.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (discount != null ? discount.hashCode() : 0);
        result = 31 * result + (usesPerCustomer != null ? usesPerCustomer.hashCode() : 0);
        result = 31 * result + (uniqueUse != null ? uniqueUse.hashCode() : 0);
        return result;
    }

}
