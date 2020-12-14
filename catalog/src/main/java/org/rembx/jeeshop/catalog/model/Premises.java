package org.rembx.jeeshop.catalog.model;

import org.rembx.jeeshop.address.Address;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Cacheable
public class Premises {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Address address;

    @OneToMany(cascade =  CascadeType.ALL, fetch = FetchType.EAGER)
    List<Schedules> schedules;

    public Premises() {
    }

    public Premises(Long id) {
        this.id = id;
    }

    public Premises(Long id, Address address, List<Schedules> schedules) {
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Schedules> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedules> schedules) {
        this.schedules = schedules;
    }
}
