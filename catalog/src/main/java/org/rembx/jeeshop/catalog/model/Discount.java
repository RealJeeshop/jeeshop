package org.rembx.jeeshop.catalog.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by remi on 20/05/14.
 */
@Entity
public class Discount {

    @Id
    @GeneratedValue
    private Long id;

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

    @Column(nullable = false)
    private String name;

    private String voucherCode;

    @ManyToMany
    private Set<Presentation> presentation;

    private Double amount;

    private Double discount;

    @ManyToMany
    private List<SKU> skus;

    @ManyToMany
    private List<Product> products;

    private Integer usesPerCustomer;

    /**
     * Cannot be used with other discounts when true
     */
    private Boolean uniqueUse;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    private Boolean disabled;

    public Long getId() {
        return id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
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
        if (disabled != null ? !disabled.equals(discount1.disabled) : discount1.disabled != null) return false;
        if (discount != null ? !discount.equals(discount1.discount) : discount1.discount != null) return false;
        if (endDate != null ? !endDate.equals(discount1.endDate) : discount1.endDate != null) return false;
        if (id != null ? !id.equals(discount1.id) : discount1.id != null) return false;
        if (name != null ? !name.equals(discount1.name) : discount1.name != null) return false;
        if (startDate != null ? !startDate.equals(discount1.startDate) : discount1.startDate != null) return false;
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
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (triggerRule != null ? triggerRule.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (voucherCode != null ? voucherCode.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (discount != null ? discount.hashCode() : 0);
        result = 31 * result + (usesPerCustomer != null ? usesPerCustomer.hashCode() : 0);
        result = 31 * result + (uniqueUse != null ? uniqueUse.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (disabled != null ? disabled.hashCode() : 0);
        return result;
    }
}
