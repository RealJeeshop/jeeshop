package org.rembx.jeeshop.catalog.domain

import jakarta.json.bind.annotation.JsonbTransient
import jakarta.persistence.*
import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlRootElement
import java.util.Objects

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Cacheable
@Table(name = "store_premises")
class Premises {
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @field:OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var address: PremisesAddress? = null

    @field:OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER, mappedBy = "premises")
    var schedules: MutableList<PremisesOpeningSchedules>? = null

    @field:ManyToOne
    @field:JsonbTransient
    var store: Store? = null

    constructor()

    constructor(id: Long?) : this() {
        this.id = id
    }

    constructor(id: Long?, address: PremisesAddress?, schedules: MutableList<PremisesOpeningSchedules>?) : this() {
        this.id = id
        this.address = address
        this.schedules = schedules
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as Premises
        return Objects.equals(id, other.id) && Objects.equals(address, other.address)
    }

    override fun hashCode(): Int = Objects.hash(id, address)

    override fun toString(): String = "Premises{id=$id, address=$address}"
}
