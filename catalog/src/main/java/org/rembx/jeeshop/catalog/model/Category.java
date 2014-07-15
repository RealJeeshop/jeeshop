package org.rembx.jeeshop.catalog.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by remi on 20/05/14.
 */
@Entity
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Cacheable
public class Category extends CatalogItem {

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(joinColumns = @JoinColumn(name = "parentCategoryId"),
            inverseJoinColumns = @JoinColumn(name = "childCategoryId"))
    @OrderColumn(name = "orderIdx")
    @XmlTransient
    private List<Category> childCategories;

    @Transient
    private List<Long> childCategoriesIds;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(joinColumns = @JoinColumn(name = "categoryId"),
            inverseJoinColumns = @JoinColumn(name = "productId"))
    @OrderColumn(name = "orderIdx")
    @XmlTransient
    private List<Product> childProducts;

    @Transient
    private List<Long> childProductsIds;

    public Category() {
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Category(Long id, String name, String description, Date startDate, Date endDate, Boolean disabled) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.disabled = disabled;
    }

    public Category(String name, String description, Date startDate, Date endDate, Boolean disabled) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.disabled = disabled;
    }

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

    public List<Long> getChildCategoriesIds() {
        return childCategoriesIds;
    }

    public void setChildCategoriesIds(List<Long> childCategoriesIds) {
        this.childCategoriesIds = childCategoriesIds;
    }

    public List<Long> getChildProductsIds() {
        return childProductsIds;
    }

    public void setChildProductsIds(List<Long> childProductsIds) {
        this.childProductsIds = childProductsIds;
    }
}
