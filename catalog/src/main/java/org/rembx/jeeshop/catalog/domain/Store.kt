package org.rembx.jeeshop.catalog.domain

import jakarta.json.bind.annotation.JsonbTransient
import jakarta.persistence.*
import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlRootElement
import java.util.Objects

/** Store entity. */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Cacheable
class Store : CatalogItem {
    @field:ManyToMany(cascade = [CascadeType.PERSIST])
    @field:JoinTable(
        joinColumns = [JoinColumn(name = "store_id")],
        inverseJoinColumns = [JoinColumn(name = "catalog_id")]
    )
    @field:OrderColumn(name = "orderIdx")
    @field:JsonbTransient
    var catalogs: MutableList<Catalog>? = null

    @field:Transient
    var catalogsIds: MutableList<Long>? = null

    @field:OneToMany(cascade = [CascadeType.PERSIST], fetch = FetchType.EAGER, mappedBy = "store")
    var premisses: MutableList<Premises>? = null

    constructor()

    constructor(name: String) : this() {
        this.name = name
    }

    constructor(id: Long?, name: String) : this(name) {
        this.id = id
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass || !super.equals(other)) return false
        return Objects.equals(premisses, (other as Store).premisses)
    }

    override fun hashCode(): Int = Objects.hash(super.hashCode(), premisses)
}
