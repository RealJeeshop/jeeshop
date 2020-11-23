package org.rembx.jeeshop.catalog.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;
import java.util.List;

/**
 * Created by remi on 20/05/14.
 */
@Entity
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Cacheable
public class Product extends CatalogItem {
    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(joinColumns = @JoinColumn(name = "productId"),
            inverseJoinColumns = @JoinColumn(name = "skuId"))
    @OrderColumn(name = "orderIdx")
    @XmlTransient
    private List<SKU> childSKUs;

    @Transient
    private List<Long> childSKUsIds;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(joinColumns = @JoinColumn(name = "productId"),
            inverseJoinColumns = @JoinColumn(name = "discountId"))
    @OrderColumn(name = "orderIdx")
    @XmlTransient
    private List<Discount> discounts;

    @Transient
    private List<Long> discountsIds;

    public Product() {
    }

    public Product(String name, String description, Date startDate, Date endDate, Boolean disabled) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.disabled = disabled;
    }

    public Product(Long id, String name, String description, Date startDate, Date endDate, Boolean disabled) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.disabled = disabled;
    }

    public List<SKU> getChildSKUs() {
        return childSKUs;
    }

    public void setChildSKUs(List<SKU> childSKUs) {
        this.childSKUs = childSKUs;
    }

    public List<Discount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<Discount> discounts) {
        this.discounts = discounts;
    }


    public List<Long> getChildSKUsIds() {
        return childSKUsIds;
    }

    public void setChildSKUsIds(List<Long> childSKUsIds) {
        this.childSKUsIds = childSKUsIds;
    }

    public List<Long> getDiscountsIds() {
        return discountsIds;
    }

    public void setDiscountsIds(List<Long> discountsIds) {
        this.discountsIds = discountsIds;
    }
}
