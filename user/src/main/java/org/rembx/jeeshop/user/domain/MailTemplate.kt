package org.rembx.jeeshop.user.domain

import jakarta.json.bind.annotation.JsonbTransient
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlType
import org.rembx.jeeshop.media.domain.Media
import java.util.Date

@Entity
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
class MailTemplate() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @field:Column(nullable = false, length = 100)
    @field:NotNull
    var name: String? = null

    @field:Size(max = 25)
    @field:Column(length = 25, nullable = true)
    var locale: String? = null

    @field:NotNull
    @field:Column(nullable = false)
    var content: String? = null

    @field:NotNull
    @field:Column(nullable = false)
    var subject: String? = null

    @field:ManyToMany
    @field:JoinTable(
        name = "MailTemplate_Media",
        joinColumns = [JoinColumn(name = "mailTemplateId")],
        inverseJoinColumns = [JoinColumn(name = "mediaId")],
    )
    @field:JsonbTransient
    var medias: Set<Media>? = null

    @field:Temporal(TemporalType.TIMESTAMP)
    var creationDate: Date? = null

    @field:Temporal(TemporalType.TIMESTAMP)
    var updateDate: Date? = null

    constructor(name: String?, locale: String?, content: String?, subject: String?) : this() {
        this.name = name
        this.locale = locale
        this.content = content
        this.subject = subject
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
        other as MailTemplate
        return content == other.content && id == other.id && locale == other.locale &&
            name == other.name && subject == other.subject
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (locale?.hashCode() ?: 0)
        result = 31 * result + (content?.hashCode() ?: 0)
        result = 31 * result + (subject?.hashCode() ?: 0)
        return result
    }
}
