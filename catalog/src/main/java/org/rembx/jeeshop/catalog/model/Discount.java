package org.rembx.jeeshop.catalog.model;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;
import java.util.List;

/**
 * Discount entity
 */
@Entity
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Cacheable
public class Discount extends CatalogItem {

    public static enum Type {
        DISCOUNT_RATE,
        ORDER_DISCOUNT_AMOUNT,
        SHIPPING_FEE_DISCOUNT_AMOUNT
    }

    public static enum Trigger {
        QUANTITY,
        AMOUNT,
        ORDER_NUMBER
    }

    public static enum ApplicableTo {
        ORDER,
        ITEM
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

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(length = 10, nullable = false)
    private ApplicableTo applicableTo;

    private Double triggerValue;

    private Double discountValue;

    @ManyToMany(mappedBy = "discounts")
    @JsonbTransient
    private List<SKU> skus;

    @ManyToMany(mappedBy = "discounts")
    @JsonbTransient
    private List<Product> products;

    @Transient
    private Boolean rateType;

    @PostLoad
    @PostPersist
    @PostUpdate
    protected void comptuteRateType() {
        rateType = (type != null && type.equals(Type.DISCOUNT_RATE));
    }

    /**
     * Cannot be used with other discounts when true
     */
    private Boolean uniqueUse;

    public Discount() {
    }

    public Discount(Long id) {
        this.id = id;
    }

    public Discount(String name, String description, ApplicableTo applicableTo, Type type, Trigger triggerRule, String voucherCode, Double discountValue, Double triggerValue, Integer usesPerCustomer, Boolean uniqueUse, Date startDate, Date endDate, Boolean disabled) {
        this.name = name;
        this.description = description;
        this.applicableTo = applicableTo;
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

    public boolean isEligible(Double itemsPrice){ // TODO continue implem  of triggerRule

        if (triggerRule != null) {
            switch (triggerRule) {
                case AMOUNT:
                    if (triggerValue != null && itemsPrice < triggerValue) {
                        return false;
                    }
                    break;
            }
        }
        return true;
    }

    public Double processDiscount(Double currentPrice, Double originItemsPrice) { // TODO continue implem  of triggerRule

        if (!isEligible(originItemsPrice)){
            return currentPrice;
        }

        switch (type) {
            case DISCOUNT_RATE:
                currentPrice = currentPrice - originItemsPrice * discountValue / 100;
                break;
            case ORDER_DISCOUNT_AMOUNT:
                currentPrice -= discountValue;
                break;
            case SHIPPING_FEE_DISCOUNT_AMOUNT:
                currentPrice -= discountValue;
        }

        return currentPrice;
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

    public ApplicableTo getApplicableTo() {
        return applicableTo;
    }

    public void setApplicableTo(ApplicableTo applicableTo) {
        this.applicableTo = applicableTo;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Discount discount1 = (Discount) o;

        if (applicableTo != discount1.applicableTo) return false;
        if (discountValue != null ? !discountValue.equals(discount1.discountValue) : discount1.discountValue != null)
            return false;
        if (triggerRule != discount1.triggerRule) return false;
        if (triggerValue != null ? !triggerValue.equals(discount1.triggerValue) : discount1.triggerValue != null)
            return false;
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
        int result = super.hashCode();
        result = 31 * result + (voucherCode != null ? voucherCode.hashCode() : 0);
        result = 31 * result + (usesPerCustomer != null ? usesPerCustomer.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (triggerRule != null ? triggerRule.hashCode() : 0);
        result = 31 * result + (applicableTo != null ? applicableTo.hashCode() : 0);
        result = 31 * result + (triggerValue != null ? triggerValue.hashCode() : 0);
        result = 31 * result + (discountValue != null ? discountValue.hashCode() : 0);
        result = 31 * result + (uniqueUse != null ? uniqueUse.hashCode() : 0);
        return result;
    }
}
