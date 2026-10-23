package org.rembx.jeeshop.catalog.domain

import jakarta.json.bind.annotation.JsonbTransient
import jakarta.persistence.*
import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlRootElement

/** Catalog entity. */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Cacheable
class Catalog : CatalogItem {
    @field:ManyToMany(cascade = [CascadeType.PERSIST])
    @field:JoinTable(
        joinColumns = [JoinColumn(name = "catalogId")],
        inverseJoinColumns = [JoinColumn(name = "categoryId")]
    )
    @field:OrderColumn(name = "orderIdx")
    @field:JsonbTransient
    var rootCategories: MutableList<Category>? = null

    @field:Transient
    var rootCategoriesIds: MutableList<Long>? = null

    constructor()

    constructor(name: String?) : this() {
        this.name = name
    }

    constructor(id: Long?, name: String?) : this(name) {
        this.id = id
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass || !super.equals(other)) return false
        return rootCategories == (other as Catalog).rootCategories
    }

    override fun hashCode(): Int = 31 * super.hashCode() + (rootCategories?.hashCode() ?: 0)
}
