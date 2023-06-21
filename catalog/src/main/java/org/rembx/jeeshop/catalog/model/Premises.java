package org.rembx.jeeshop.catalog.model;

import javax.json.bind.annotation.JsonbTransient;
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
@Table(name = "store_premises")
public class Premises {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    PremisesAddress address;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "premises")
    List<PremisesOpeningSchedules> schedules;

    @ManyToOne
    @JsonbTransient
    Store store;

    public Premises() {
    }

    public Premises(Long id) {
        this.id = id;
    }

    public Premises(Long id, PremisesAddress address, List<PremisesOpeningSchedules> schedules) {
        this.id = id;
        this.address = address;
        this.schedules = schedules;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PremisesAddress getAddress() {
        return address;
    }

    public void setAddress(PremisesAddress address) {
        this.address = address;
    }

    public List<PremisesOpeningSchedules> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<PremisesOpeningSchedules> schedules) {
        this.schedules = schedules;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Premises premises = (Premises) o;
        return Objects.equals(id, premises.id) && Objects.equals(address, premises.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address);
    }

    @Override
    public String toString() {
        return "Premises{" +
                "id=" + id +
                ", address=" + address +
                '}';
    }
}
