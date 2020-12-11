package org.rembx.jeeshop.catalog.model;

import org.rembx.jeeshop.address.Address;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Address address;

    @JsonbTransient
    @Size(max = 255)
    @Column(length = 255)
    String openingHours;

    @OneToMany(cascade =  CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "store")
    List<Schedules> schedules;

    @Email
    @NotNull
    @Column(nullable = false, length = 100)
    String owner;

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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public List<Schedules> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedules> schedules) {
        this.schedules = schedules;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
