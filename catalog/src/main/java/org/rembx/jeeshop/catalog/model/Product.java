package org.rembx.jeeshop.catalog.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by remi on 20/05/14.
 */
@Entity
public class Product {

    @Id
    @GeneratedValue
    private Long id;

    @Size(max = 100)
    @Column(unique = true, nullable = false, length = 100)
    @NotNull
    private String name;

    @Size(max = 255)
    @Column(length = 255)
    private String description;

    @OneToMany (mappedBy = "product")
    @OrderColumn(name="orderIdx")
    private List<SKU> childSKUs;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    private Boolean disabled;

    @ManyToMany
    @JoinTable(joinColumns = @JoinColumn(name = "productId"),
            inverseJoinColumns = @JoinColumn(name = "presentationId"))
    private Set<Presentation> presentations;

    @ManyToMany
    @JoinTable(joinColumns = @JoinColumn(name = "productId"),
            inverseJoinColumns = @JoinColumn(name = "discountId"))
    @OrderColumn(name="orderIdx")
    private List <Discount> discounts;

    public Product() {
    }

    public Product(String name, Date startDate, Date endDate, Boolean disabled) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.disabled = disabled;
    }

    @PrePersist
    private void prePersist() {
        if (disabled == null) {
            disabled = false;
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SKU> getChildSKUs() {
        return childSKUs;
    }

    public void setChildSKUs(List<SKU> childSKUs) {
        this.childSKUs = childSKUs;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (disabled != null ? !disabled.equals(product.disabled) : product.disabled != null) return false;
        if (endDate != null ? !endDate.equals(product.endDate) : product.endDate != null) return false;
        if (name != null ? !name.equals(product.name) : product.name != null) return false;
        if (startDate != null ? !startDate.equals(product.startDate) : product.startDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (disabled != null ? disabled.hashCode() : 0);
        return result;
    }
}
