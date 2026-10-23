package org.rembx.jeeshop.catalog.infrastructure.api

import io.quarkus.hibernate.orm.PersistenceUnit
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.Dependent
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.transaction.UserTransaction
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.rembx.jeeshop.catalog.domain.CatalogItem
import org.rembx.jeeshop.catalog.infrastructure.repository.CatalogPersistenceUnit
import org.rembx.jeeshop.catalog.domain.Presentation
import org.rembx.jeeshop.rest.WebApplicationException
import org.rembx.jeeshop.role.JeeshopRoles.ADMIN
import org.rembx.jeeshop.role.JeeshopRoles.STORE_ADMIN

import jakarta.transaction.Transactional.TxType.REQUIRES_NEW

/** Sub-resource of CatalogItem resources dedicated to a Presentation instance. */
@Dependent
class PresentationResource<T : CatalogItem>(
    @PersistenceUnit(CatalogPersistenceUnit.NAME)
    private val entityManager: EntityManager,
    private val transaction: UserTransaction?
) {
    private var presentation: Presentation? = null
    private var parentCatalogItem: CatalogItem? = null
    private var locale: String? = null
    private var clazz: Class<T>? = null

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun find(): Presentation {
        checkEntityNotNull()
        return presentation!!
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(REQUIRES_NEW)
    @RolesAllowed(STORE_ADMIN, ADMIN)
    fun delete() {
        checkEntityNotNull()
        refreshCatalogItem()
        parentCatalogItem!!.presentationByLocale!!.remove(presentation!!.locale)
        entityManager.merge(parentCatalogItem)
        entityManager.remove(entityManager.merge(presentation))
    }

    private fun refreshCatalogItem() {
        parentCatalogItem = entityManager.find(clazz!!, parentCatalogItem!!.id)
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @RolesAllowed(STORE_ADMIN, ADMIN)
    fun createLocalizedPresentation(presentation: Presentation): Presentation {
        if (this.presentation != null) throw WebApplicationException(Response.Status.BAD_REQUEST)
        presentation.locale = locale
        entityManager.persist(presentation)
        refreshCatalogItem()
        parentCatalogItem!!.presentationByLocale!!.put(locale!!, presentation)
        entityManager.merge(parentCatalogItem)
        return presentation
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(REQUIRES_NEW)
    @RolesAllowed(STORE_ADMIN, ADMIN)
    fun modifyLocalizedPresentation(presentation: Presentation): Presentation {
        checkEntityNotNull()
        if (this.presentation == null) throw WebApplicationException(Response.Status.BAD_REQUEST)
        entityManager.merge(presentation)
        return presentation
    }

    private fun checkEntityNotNull() {
        if (presentation == null) throw WebApplicationException(Response.Status.NOT_FOUND)
    }

    fun init(
        clazz: Class<T>,
        parentCatalogItem: CatalogItem?,
        locale: String?,
        presentation: Presentation?
    ): PresentationResource<T> {
        this.clazz = clazz
        this.presentation = presentation
        this.locale = locale
        this.parentCatalogItem = parentCatalogItem
        return this
    }
}
