package org.rembx.jeeshop.catalog.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by remi on 20/05/14.
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Catalog extends CatalogItem{

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(joinColumns = @JoinColumn(name = "catalogId"),
            inverseJoinColumns = @JoinColumn(name = "categoryId"))
    @OrderColumn(name="orderIdx")
    private List<Category> rootCategories;

    public Catalog() {
    }

    public Catalog(String name) {
        this.name = name;
    }

    public Catalog(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public List<Category> getRootCategories() {
        return rootCategories;
    }

    public void setRootCategories(List<Category> rootCategories) {
        this.rootCategories = rootCategories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Catalog catalog = (Catalog) o;

        if (rootCategories != null ? !rootCategories.equals(catalog.rootCategories) : catalog.rootCategories != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (rootCategories != null ? rootCategories.hashCode() : 0);
        return result;
    }
}
