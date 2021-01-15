package org.rembx.jeeshop.catalog.model;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Cacheable
@Table(name = "premises_schedules")
public class PremisesOpeningSchedules {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne
    @JsonbTransient
    private Premises premises;

    @Enumerated(EnumType.ORDINAL)
    @NotNull
    @Column(name = "dayoftheweek", nullable = false)
    DayOfWeek dayOfWeek;

    @Column(name = "time_open")
    LocalTime timeOpen;

    @Column(name = "time_close")
    LocalTime timeClose;

    public PremisesOpeningSchedules() {
    }

    public PremisesOpeningSchedules(Long id) {
        this.id = id;
    }

    public PremisesOpeningSchedules(Store store, DayOfWeek dayOfWeek, LocalTime timeOpen, LocalTime timeClose) {
        this.dayOfWeek = dayOfWeek;
        this.timeOpen = timeOpen;
        this.timeClose = timeClose;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PremisesOpeningSchedules that = (PremisesOpeningSchedules) o;
        return Objects.equals(id, that.id) && dayOfWeek == that.dayOfWeek && Objects.equals(timeOpen, that.timeOpen) && Objects.equals(timeClose, that.timeClose);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dayOfWeek, timeOpen, timeClose);
    }

    @Override
    public String toString() {
        return "PremisesOpeningSchedules{" +
                "id=" + id +
                ", dayOfWeek=" + dayOfWeek +
                ", timeOpen=" + timeOpen +
                ", timeClose=" + timeClose +
                '}';
    }
}
