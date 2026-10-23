package org.rembx.jeeshop.user.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import jakarta.validation.constraints.NotNull
import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlType
import java.util.Date

@Entity
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
class Newsletter() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @field:Column(nullable = false, length = 100)
    @field:NotNull
    var name: String? = null

    @field:Column(nullable = false, length = 100)
    @field:NotNull
    var mailTemplateName: String? = null

    @field:Temporal(TemporalType.TIMESTAMP)
    var creationDate: Date? = null

    @field:Temporal(TemporalType.TIMESTAMP)
    var updateDate: Date? = null

    @field:Temporal(TemporalType.TIMESTAMP)
    var dueDate: Date? = null

    constructor(name: String?, dueDate: Date?) : this() {
        this.name = name
        this.dueDate = dueDate
    }

    @PrePersist
    fun prePersist() {
        creationDate = Date()
    }

    @PreUpdate
    fun preUpdate() {
        updateDate = Date()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as Newsletter
        return id == other.id && name == other.name && mailTemplateName == other.mailTemplateName &&
            creationDate == other.creationDate && updateDate == other.updateDate && dueDate == other.dueDate
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (mailTemplateName?.hashCode() ?: 0)
        result = 31 * result + (creationDate?.hashCode() ?: 0)
        result = 31 * result + (updateDate?.hashCode() ?: 0)
        result = 31 * result + (dueDate?.hashCode() ?: 0)
        return result
    }
}
