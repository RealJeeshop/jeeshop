package org.rembx.jeeshop.catalog.domain

import jakarta.json.bind.annotation.JsonbTransient
import jakarta.persistence.*
import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlType
import java.util.Date

/** Category entity. */
@Entity
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Cacheable
class Category : CatalogItem {
    @field:ManyToMany(cascade = [CascadeType.PERSIST])
    @field:JoinTable(
        joinColumns = [JoinColumn(name = "parentCategoryId")],
        inverseJoinColumns = [JoinColumn(name = "childCategoryId")]
    )
    @field:OrderColumn(name = "orderIdx")
    @field:JsonbTransient
    var childCategories: MutableList<Category>? = null

    @field:Transient
    var childCategoriesIds: MutableList<Long>? = null

    @field:ManyToMany(cascade = [CascadeType.PERSIST])
    @field:JoinTable(
        joinColumns = [JoinColumn(name = "categoryId")],
        inverseJoinColumns = [JoinColumn(name = "productId")]
    )
    @field:OrderColumn(name = "orderIdx")
    @field:JsonbTransient
    var childProducts: MutableList<Product>? = null

    @field:Transient
    var childProductsIds: MutableList<Long>? = null

    constructor()

    constructor(id: Long?, name: String?) : this() {
        this.id = id
        this.name = name
    }

    constructor(name: String?, description: String?) : this() {
        this.name = name
        this.description = description
    }

    constructor(id: Long?, name: String?, description: String?, startDate: Date?, endDate: Date?, disabled: Boolean?) : this() {
        this.id = id
        this.name = name
        this.description = description
        this.startDate = startDate
        this.endDate = endDate
        this.disabled = disabled
    }

    constructor(name: String?, description: String?, startDate: Date?, endDate: Date?, disabled: Boolean?, owner: String?) : this() {
        this.name = name
        this.description = description
        this.startDate = startDate
        this.endDate = endDate
        this.disabled = disabled
        this.owner = owner
    }
}
