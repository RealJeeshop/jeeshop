package org.rembx.jeeshop.admin

import jakarta.ws.rs.ApplicationPath
import jakarta.ws.rs.core.Application
import org.rembx.jeeshop.catalog.infrastructure.api.Catalogs
import org.rembx.jeeshop.catalog.infrastructure.api.Categories
import org.rembx.jeeshop.catalog.infrastructure.api.Discounts
import org.rembx.jeeshop.catalog.infrastructure.api.Products
import org.rembx.jeeshop.catalog.infrastructure.api.SKUs
import org.rembx.jeeshop.catalog.infrastructure.api.Stores
import org.rembx.jeeshop.media.infrastructure.api.Medias
import org.rembx.jeeshop.order.infrastructure.api.EligibleDiscounts
import org.rembx.jeeshop.order.infrastructure.api.Orders
import org.rembx.jeeshop.rest.WebApplicationExceptionMapper
import org.rembx.jeeshop.user.infrastructure.api.MailTemplates
import org.rembx.jeeshop.user.infrastructure.api.Users

@ApplicationPath("/rs")
class ApplicationConfig : Application() {
    override fun getClasses(): Set<Class<*>> = setOf(
        Stores::class.java,
        Catalogs::class.java,
        Categories::class.java,
        Products::class.java,
        SKUs::class.java,
        Discounts::class.java,
        Users::class.java,
        Medias::class.java,
        MailTemplates::class.java,
        Orders::class.java,
        EligibleDiscounts::class.java,
        WebApplicationExceptionMapper::class.java,
    )
}
