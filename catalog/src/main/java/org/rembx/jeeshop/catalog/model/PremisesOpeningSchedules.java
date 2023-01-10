package org.rembx.jeeshop.catalog.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Cacheable
public class PremisesOpeningSchedules {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private Store store;

    @Enumerated(EnumType.ORDINAL)
    @NotNull
    @Column(nullable = false)
    DayOfWeek dayOfWeek;

    @Column
    LocalTime timeOpen;

    @Column
    LocalTime timeClose;

    public PremisesOpeningSchedules() {
    }

    public PremisesOpeningSchedules(Long id) {
        this.id = id;
    }

    public PremisesOpeningSchedules(Store store, DayOfWeek dayOfWeek, LocalTime timeOpen, LocalTime timeClose) {
        this.store = store;
        this.dayOfWeek = dayOfWeek;
        this.timeOpen = timeOpen;
        this.timeClose = timeClose;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getTimeOpen() {
        return timeOpen;
    }

    public void setTimeOpen(LocalTime timeOpen) {
        this.timeOpen = timeOpen;
    }

    public LocalTime getTimeClose() {
        return timeClose;
    }

    public void setTimeClose(LocalTime timeClose) {
        this.timeClose = timeClose;
    }
}
