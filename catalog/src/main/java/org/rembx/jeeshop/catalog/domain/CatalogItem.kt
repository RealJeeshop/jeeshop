package org.rembx.jeeshop.catalog.domain

import jakarta.json.bind.annotation.JsonbTransient
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.rembx.jeeshop.util.DateUtil.dateToLocalDateTime
import org.rembx.jeeshop.util.LocaleUtil.FALLBACK
import org.rembx.jeeshop.util.LocaleUtil.getLocaleCode
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.util.*

/**
 * Parent class of all catalog node types.
 * Group common properties used by catalog node elements.
 */
@MappedSuperclass
abstract class CatalogItem {
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    /** Technical name */
    @field:Column(nullable = false, length = 50)
    @field:NotNull
    @field:Size(max = 50)
    var name: String? = null

    /** Technical description */
    @field:Size(max = 255)
    @field:Column(length = 255)
    var description: String? = null

    /** When true, entity is disabled, and not visible by end customer */
    var disabled: Boolean? = null

    /** When defined, entity is not visible when current date is lower than it */
    @field:Temporal(TemporalType.TIMESTAMP)
    var startDate: Date? = null

    /** When defined, entity is not visible when current date is greater than it */
    @field:Temporal(TemporalType.TIMESTAMP)
    var endDate: Date? = null

    /** True when entity is visible */
    @field:Transient
    var visible: Boolean = false

    @field:ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE])
    @field:JoinTable(
        joinColumns = [JoinColumn(name = "catalogItemId", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "presentationId")]
    )
    @field:MapKey(name = "locale")
    @field:JsonbTransient
    var presentationByLocale: MutableMap<String, Presentation>? = null

    @field:Transient
    var localizedPresentation: Presentation? = null

    @field:Email
    @field:NotNull
    @field:Column(nullable = false, length = 100)
    var owner: String? = null

    @PrePersist
    @PreUpdate
    protected fun computeDisabled() {
        if (disabled == null) disabled = false
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    protected fun computeIsVisible() {
        val now = ZonedDateTime.now()
        visible = !(isDisabled() == true ||
            (endDate != null && dateToLocalDateTime(endDate!!).isBefore(now)) ||
            (startDate != null && dateToLocalDateTime(startDate!!).isAfter(now)))
    }

    fun setLocalizedPresentation(locale: String?) {
        val presentations = presentationByLocale
        if (presentations.isNullOrEmpty()) return

        var selected = presentations[getLocaleCode(locale)]
        if (selected == null && locale != null) {
            val parts = locale.split("_")
            if (parts.size > 1) selected = presentations[getLocaleCode(parts[0])]
        }
        if (selected == null) selected = presentations[FALLBACK.toString()]
        localizedPresentation = selected
    }

    fun isDisabled(): Boolean? = disabled
    fun isVisible(): Boolean = visible

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as CatalogItem
        return description == other.description &&
            disabled == other.disabled &&
            endDate == other.endDate &&
            id == other.id &&
            name == other.name &&
            startDate == other.startDate
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (disabled?.hashCode() ?: 0)
        result = 31 * result + (startDate?.hashCode() ?: 0)
        result = 31 * result + (endDate?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String =
        "CatalogItem{id=$id, name='$name', description='$description', disabled=$disabled, startDate=$startDate, endDate=$endDate}"

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val locales = SimpleDateFormat.getAvailableLocales().drop(1)
            val values = sortedSetOf<String>()
            var localeLength = 0
            for (locale in locales) {
                localeLength = maxOf(localeLength, locale.toString().length)
                values.add("{displayName:\"${locale.getDisplayName(Locale.ENGLISH)}\",name:\"$locale\"},")
            }
            println(localeLength)
            var buffer = ""
            for (value in values) {
                buffer += value
                if (buffer.length > 160) {
                    println(buffer)
                    buffer = ""
                }
            }
        }
    }
}
