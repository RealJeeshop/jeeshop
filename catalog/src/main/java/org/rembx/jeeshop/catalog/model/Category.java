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
public class Category extends CatalogItem{

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(joinColumns = @JoinColumn(name = "parentCategoryId"),
            inverseJoinColumns = @JoinColumn(name = "childCategoryId"))
    @OrderColumn(name="orderIdx")
    @XmlTransient
    private List<Category> childCategories;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(joinColumns = @JoinColumn(name = "categoryId"),
            inverseJoinColumns = @JoinColumn(name = "productId"))
    @OrderColumn(name="orderIdx")
    @XmlTransient
    private List<Product> childProducts;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "categoryId",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "presentationId"))
    private Set<Presentation> presentations;

    public Category() {
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Category(String name, String description, Date startDate, Date endDate, Boolean disabled) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.disabled = disabled;
    }


    @XmlTransient
    public List<Category> getChildCategories() {
        return childCategories;
    }

    public void setChildCategories(List<Category> childCategories) {
        this.childCategories = childCategories;
    }

    public List<Product> getChildProducts() {
        return childProducts;
    }

    public void setChildProducts(List<Product> childProducts) {
        this.childProducts = childProducts;
    }

    public Set<Presentation> getPresentations() {
        return presentations;
    }

    public void setPresentations(Set<Presentation> presentations) {
        this.presentations = presentations;
    }


}
