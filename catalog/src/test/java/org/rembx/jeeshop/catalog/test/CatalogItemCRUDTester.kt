package org.rembx.jeeshop.catalog.test

import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence
import jakarta.ws.rs.core.SecurityContext
import org.apache.http.auth.BasicUserPrincipal
import org.rembx.jeeshop.catalog.infrastructure.api.CatalogItemService
import org.rembx.jeeshop.catalog.domain.CatalogItem
import org.rembx.jeeshop.catalog.infrastructure.repository.CatalogPersistenceUnit
import org.rembx.jeeshop.role.JeeshopRoles
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class CatalogItemCRUDTester<T : CatalogItem>(private val itemClass: Class<T>) {
    private val entityManagerFactory: EntityManagerFactory = Persistence.createEntityManagerFactory(CatalogPersistenceUnit.NAME)
    private val entityManagerInternal: EntityManager = entityManagerFactory.createEntityManager()
    private var securityContextInternal: SecurityContext = mock(SecurityContext::class.java)
    private var testCatalogInternal: TestCatalog = TestCatalog.getInstance()
    private var serviceInternal: CatalogItemService<T>? = null

    fun getService(): CatalogItemService<T>? = serviceInternal

    fun setService(service: CatalogItemService<T>) {
        this.serviceInternal = service
    }

    fun test_create(catalogItem: T): T {
        entityManagerInternal.transaction.begin()
        serviceInternal!!.create(securityContextInternal, catalogItem)
        entityManagerInternal.transaction.commit()
        return refreshCatalogItem(catalogItem)
    }

    fun test_delete(catalogItem: T) {
        entityManagerInternal.transaction.begin()
        entityManagerInternal.persist(catalogItem)
        entityManagerInternal.transaction.commit()

        entityManagerInternal.transaction.begin()
        serviceInternal!!.delete(securityContextInternal, catalogItem.id)
        entityManagerInternal.transaction.commit()
    }

    fun test_modify(catalogItem: T) {
        serviceInternal!!.modify(securityContextInternal, catalogItem)
    }

    private fun refreshCatalogItem(store: T): T {
        val actualStore = entityManagerInternal.find(itemClass, store.id)
        entityManagerInternal.remove(store)
        return actualStore
    }

    fun setAdminUser() {
        `when`(securityContextInternal.userPrincipal).thenReturn(BasicUserPrincipal("admin@jeeshop.org"))
        `when`(securityContextInternal.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(true)
    }

    fun setStoreAdminUser() {
        `when`(securityContextInternal.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(false)
        `when`(securityContextInternal.isUserInRole(JeeshopRoles.STORE_ADMIN)).thenReturn(true)
        `when`(securityContextInternal.userPrincipal).thenReturn(BasicUserPrincipal(TestCatalog.OWNER))
    }

    fun setSAnotherStoreAdminUser() {
        `when`(securityContextInternal.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(false)
        `when`(securityContextInternal.isUserInRole(JeeshopRoles.STORE_ADMIN)).thenReturn(true)
        `when`(securityContextInternal.userPrincipal).thenReturn(BasicUserPrincipal("test@test.org"))
    }

    fun setPublicUser() {
        `when`(securityContextInternal.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(false)
        `when`(securityContextInternal.isUserInRole(JeeshopRoles.STORE_ADMIN)).thenReturn(false)
    }

    fun getEntityManager(): EntityManager = entityManagerInternal
    fun getSecurityContext(): SecurityContext = securityContextInternal
    fun getFixtures(): TestCatalog = testCatalogInternal
}
