package org.rembx.jeeshop.catalog.model;

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
    PremisesAddress address;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<PremisesOpeningSchedules> schedules;

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
}
