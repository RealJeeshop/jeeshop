package org.rembx.jeeshop.catalog.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by remi on 20/05/14.
 */
@Entity
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class Product extends CatalogItem{
    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(joinColumns = @JoinColumn(name = "productId"),
            inverseJoinColumns = @JoinColumn(name = "skuId"))
    @OrderColumn(name="orderIdx")
    @XmlTransient
    private List<SKU> childSKUs;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "productId"),
            inverseJoinColumns = @JoinColumn(name = "presentationId"))
    private Set<Presentation> presentations;

    @ManyToMany
    @JoinTable(joinColumns = @JoinColumn(name = "productId"),
            inverseJoinColumns = @JoinColumn(name = "discountId"))
    @OrderColumn(name="orderIdx")
    @XmlTransient
    private List <Discount> discounts;

    public Product() {
    }

    public Product(String name, Date startDate, Date endDate, Boolean disabled) {
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

    public Set<Presentation> getPresentations() {
        return presentations;
    }

    public void setPresentations(Set<Presentation> presentations) {
        this.presentations = presentations;
    }

    public List<Discount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<Discount> discounts) {
        this.discounts = discounts;
    }

}
