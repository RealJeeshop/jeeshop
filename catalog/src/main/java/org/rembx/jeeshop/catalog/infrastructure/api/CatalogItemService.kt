package org.rembx.jeeshop.catalog.infrastructure.api

import jakarta.validation.constraints.NotNull
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.rembx.jeeshop.catalog.domain.CatalogItem
import org.rembx.jeeshop.rest.WebApplicationException
import org.rembx.jeeshop.role.JeeshopRoles.ADMIN
import org.rembx.jeeshop.role.JeeshopRoles.STORE_ADMIN

interface CatalogItemService<T : CatalogItem> {
    fun findAll(search: String?, start: Int?, size: Int?, orderBy: String?, isDesc: Boolean?, locale: String?): List<T>
    fun find(securityContext: SecurityContext?, @NotNull productId: Long?, locale: String?): T
    fun create(securityContext: SecurityContext?, item: T): T
    fun modify(securityContext: SecurityContext?, item: T): T
    fun delete(securityContext: SecurityContext?, itemId: Long?)
    fun findPresentationsLocales(securityContext: SecurityContext?, @NotNull catalogId: Long?): Set<String>
    fun findPresentationByLocale(@NotNull productId: Long?, @NotNull locale: String): PresentationResource<T>

    fun attachOwner(securityContext: SecurityContext?, catalogItem: CatalogItem) {
        if (securityContext == null) return
        if (securityContext.isUserInRole(ADMIN) && catalogItem.owner == null) {
            throw WebApplicationException(Response.Status.BAD_REQUEST)
        } else if (securityContext.isUserInRole(STORE_ADMIN) && catalogItem.owner == null) {
            catalogItem.owner = securityContext.userPrincipal?.name
        }
    }
}
