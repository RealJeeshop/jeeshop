package org.rembx.jeeshop.catalog.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

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
}
