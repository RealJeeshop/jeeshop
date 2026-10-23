package org.rembx.jeeshop.user.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "`role`")
class Role() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @field:NotNull
    @field:Column(unique = true, nullable = false)
    @field:Enumerated(EnumType.STRING)
    var name: RoleName? = null

    constructor(name: RoleName?) : this() {
        this.name = name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as Role
        return id == other.id && name == other.name
    }

    override fun hashCode(): Int = 31 * (id?.hashCode() ?: 0) + (name?.hashCode() ?: 0)
}
