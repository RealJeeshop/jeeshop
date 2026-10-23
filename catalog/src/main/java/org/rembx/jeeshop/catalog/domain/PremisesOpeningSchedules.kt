package org.rembx.jeeshop.catalog.domain

import jakarta.json.bind.annotation.JsonbTransient
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlType
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.Objects

@Entity
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Cacheable
@Table(name = "premises_schedules")
class PremisesOpeningSchedules {
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @field:ManyToOne
    @field:JsonbTransient
    var premises: Premises? = null

    @field:Enumerated(EnumType.ORDINAL)
    @field:NotNull
    @field:Column(name = "dayoftheweek", nullable = false)
    var dayOfWeek: DayOfWeek? = null

    @field:Column(name = "time_open")
    var timeOpen: LocalTime? = null

    @field:Column(name = "time_close")
    var timeClose: LocalTime? = null

    constructor()

    constructor(id: Long?) : this() {
        this.id = id
    }

    constructor(store: Store?, dayOfWeek: DayOfWeek?, timeOpen: LocalTime?, timeClose: LocalTime?) : this() {
        this.dayOfWeek = dayOfWeek
        this.timeOpen = timeOpen
        this.timeClose = timeClose
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as PremisesOpeningSchedules
        return id == other.id && dayOfWeek == other.dayOfWeek &&
            timeOpen == other.timeOpen && timeClose == other.timeClose
    }

    override fun hashCode(): Int = Objects.hash(id, dayOfWeek, timeOpen, timeClose)

    override fun toString(): String =
        "PremisesOpeningSchedules{id=$id, dayOfWeek=$dayOfWeek, timeOpen=$timeOpen, timeClose=$timeClose}"
}
