package org.rembx.jeeshop.catalog;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by remi on 20/05/14.
 */
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false, length = 50)
    @NotNull
    @Size(max = 50)
    private String name;

    private String description;

    @ManyToMany
    private List<Category> childCategories;

    @ManyToMany
    private List<Product> childProducts;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    private Boolean disabled;

    @ManyToMany
    private Set<Promotion> promotions;

    public Category(String name, String description, List<Category> childCategories, List<Product> childProducts, Date startDate, Date endDate, Boolean disabled, Set<Promotion> promotions) {
        this.name = name;
        this.description = description;
        this.childCategories = childCategories;
        this.childProducts = childProducts;
        this.startDate = startDate;
        this.endDate = endDate;
        this.disabled = disabled;
        this.promotions = promotions;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Category> getChildCategories() {
        return childCategories;
    }

    public List<Product> getChildProducts() {
        return childProducts;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public Set<Promotion> getPromotion() {
        return promotions;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        if (description != null ? !description.equals(category.description) : category.description != null)
            return false;
        if (disabled != null ? !disabled.equals(category.disabled) : category.disabled != null) return false;
        if (endDate != null ? !endDate.equals(category.endDate) : category.endDate != null) return false;
        if (id != null ? !id.equals(category.id) : category.id != null) return false;
        if (name != null ? !name.equals(category.name) : category.name != null) return false;
        if (startDate != null ? !startDate.equals(category.startDate) : category.startDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (disabled != null ? disabled.hashCode() : 0);
        return result;
    }
}
