package org.rembx.jeeshop.media.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.Size

@Entity
class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @field:Size(max = 255)
    @field:Column(nullable = false, length = 255)
    var uri: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as Media
        return id == other.id && uri == other.uri
    }

    override fun hashCode(): Int = 31 * (id?.hashCode() ?: 0) + (uri?.hashCode() ?: 0)
}
