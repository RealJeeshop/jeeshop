package org.rembx.jeeshop.catalog.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Objects;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Cacheable
public class Store extends CatalogItem {

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(joinColumns = @JoinColumn(name = "storeId"),
            inverseJoinColumns = @JoinColumn(name = "catalogId"))
    @OrderColumn(name="orderIdx")
    List<Catalog> catalogs;

    @Transient
    List<Long> catalogsIds;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<Premises> premisses;

    public Store() {
    }

    public Store(String name) {
        this.name = name;
    }

    public Store(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public List<Catalog> getCatalogs() {
        return catalogs;
    }

    public void setCatalogs(List<Catalog> catalogs) {
        this.catalogs = catalogs;
    }

    public List<Premises> getPremisses() {
        return premisses;
    }

    public void setPremisses(List<Premises> premisses) {
        this.premisses = premisses;
    }

    public List<Long> getCatalogsIds() {
        return catalogsIds;
    }

    public void setCatalogsIds(List<Long> catalogsIds) {
        this.catalogsIds = catalogsIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Store store = (Store) o;
        return Objects.equals(premisses, store.premisses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), premisses);
    }
}
