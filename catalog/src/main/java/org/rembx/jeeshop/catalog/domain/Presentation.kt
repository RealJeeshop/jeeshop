package org.rembx.jeeshop.catalog.domain

import jakarta.json.bind.annotation.JsonbTransient
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlType
import org.rembx.jeeshop.media.domain.Media

/** Presentation data associated with catalog items. */
@Entity
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Cacheable
class Presentation {
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @field:NotNull
    @field:Size(min = 1, max = 25)
    @field:Column(length = 25, nullable = false)
    var locale: String? = null

    @field:Size(max = 255)
    @field:Column(length = 255)
    var displayName: String? = null

    @field:Size(max = 255)
    @field:Column(length = 255)
    var promotion: String? = null

    @field:Size(max = 1000)
    @field:Column(length = 1000)
    var shortDescription: String? = null

    @field:Size(max = 2000)
    @field:Column(length = 2000)
    var mediumDescription: String? = null

    @field:Size(max = 5000)
    @field:Column(length = 5000)
    var longDescription: String? = null

    @field:ManyToOne(cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE])
    @field:JoinColumn(referencedColumnName = "id")
    var thumbnail: Media? = null

    @field:ManyToOne(cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE])
    @field:JoinColumn(referencedColumnName = "id")
    var smallImage: Media? = null

    @field:ManyToOne(cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE])
    @field:JoinColumn(referencedColumnName = "id")
    var largeImage: Media? = null

    @field:ManyToOne(cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE])
    @field:JoinColumn(referencedColumnName = "id")
    var video: Media? = null

    @field:ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE])
    @field:JoinTable(
        joinColumns = [JoinColumn(name = "presentationId")],
        inverseJoinColumns = [JoinColumn(name = "mediaId")]
    )
    @field:OrderColumn(name = "orderIdx")
    @field:JsonbTransient
    var otherMedia: MutableList<Media>? = null

    @field:ElementCollection(fetch = FetchType.EAGER)
    @field:MapKeyColumn(name = "name")
    @field:Column(name = "value")
    @field:CollectionTable(name = "Presentation_Feature", joinColumns = [JoinColumn(name = "presentationId")])
    var features: MutableMap<String, String>? = null

    constructor()

    constructor(locale: String, displayName: String?, shortDescription: String?, description: String?) : this() {
        this.locale = locale
        this.displayName = displayName
        this.shortDescription = shortDescription
        this.longDescription = description
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as Presentation
        return displayName == other.displayName &&
            id == other.id &&
            locale == other.locale &&
            shortDescription == other.shortDescription
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (locale?.hashCode() ?: 0)
        result = 31 * result + (displayName?.hashCode() ?: 0)
        result = 31 * result + (shortDescription?.hashCode() ?: 0)
        return result
    }
}
