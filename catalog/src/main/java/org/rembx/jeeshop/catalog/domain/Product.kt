package org.rembx.jeeshop.catalog.domain

import jakarta.persistence.*
import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlType
import java.util.Date

/** Product entity. */
@Entity
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Cacheable
class Product : CatalogItem {
    @field:ManyToMany(cascade = [CascadeType.PERSIST])
    @field:JoinTable(
        joinColumns = [JoinColumn(name = "productId")],
        inverseJoinColumns = [JoinColumn(name = "skuId")]
    )
    @field:OrderColumn(name = "orderIdx")
    var childSKUs: MutableList<SKU>? = null

    @field:Transient
    var childSKUsIds: MutableList<Long>? = null

    @field:ManyToMany(cascade = [CascadeType.PERSIST])
    @field:JoinTable(
        joinColumns = [JoinColumn(name = "productId")],
        inverseJoinColumns = [JoinColumn(name = "discountId")]
    )
    @field:OrderColumn(name = "orderIdx")
    var discounts: MutableList<Discount>? = null

    @field:Transient
    var discountsIds: MutableList<Long>? = null

    constructor()

    constructor(id: Long?, name: String?) : this() {
        this.id = id
        this.name = name
    }

    constructor(name: String?) : this() {
        this.name = name
    }

    constructor(name: String?, description: String?, startDate: Date?, endDate: Date?, disabled: Boolean?, owner: String?) : this() {
        this.name = name
        this.description = description
        this.startDate = startDate
        this.endDate = endDate
        this.disabled = disabled
        this.owner = owner
    }

    constructor(id: Long?, name: String?, description: String?, startDate: Date?, endDate: Date?, disabled: Boolean?) : this() {
        this.id = id
        this.name = name
        this.description = description
        this.startDate = startDate
        this.endDate = endDate
        this.disabled = disabled
    }
}
