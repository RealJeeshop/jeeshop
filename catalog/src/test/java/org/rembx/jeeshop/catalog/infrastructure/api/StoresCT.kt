package org.rembx.jeeshop.catalog.infrastructure.api

import jakarta.ws.rs.core.Response
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.rembx.jeeshop.catalog.infrastructure.repository.CatalogItemRepositoryImpl
import org.rembx.jeeshop.catalog.domain.Store
import org.rembx.jeeshop.catalog.test.CatalogItemCRUDTester
import org.rembx.jeeshop.catalog.test.TestCatalog
import org.rembx.jeeshop.rest.WebApplicationException

class StoresCT {
    private lateinit var localService: Stores
    private lateinit var tester: CatalogItemCRUDTester<Store>

    @BeforeEach
    fun setupClass() {
        tester = CatalogItemCRUDTester(Store::class.java)
        localService = Stores(tester.getEntityManager(), CatalogItemRepositoryImpl(tester.getEntityManager()), null)
        tester.setService(localService)
    }

    @Test
    fun findAll_shouldReturnNoneEmptyList() {
        assertThat(localService.findAll(null, null, null, null, null, null)).isNotEmpty
    }

    @Test
    fun find_shouldLoadSchedules() {
        tester.setAdminUser()

        val store = localService.find(tester.getSecurityContext(), 2L, null)
        assertThat(store).isNotNull
    }

    @Test
    fun find_shouldLoadNonVisibleItem_for_admin() {
        tester.setAdminUser()
        val store = localService.find(tester.getSecurityContext(), 1L, null)
        assertThat(store).isNotNull
    }

    @Test
    fun find_shouldLoadNonVisibleItem_for_store_admin() {
        tester.setStoreAdminUser()
        val store = localService.find(tester.getSecurityContext(), 1L, null)
        assertThat(store).isNotNull
    }

    @Test
    fun create_shouldSetupOwner_for_admin() {
        tester.setAdminUser()
        val store = Store("Superstore")
        store.owner = TestCatalog.OWNER

        val actualStore = tester.test_create(store)

        assertThat(actualStore).isNotNull
        assertThat(actualStore.owner).isEqualTo(TestCatalog.OWNER)
    }

    @Test
    fun create_shouldThrowBadRequest_whenOwnerIsNull_for_admin() {
        tester.setAdminUser()
        val store = Store("Superstore")

        try {
            tester.test_create(store)
            fail("should have thrown an exception")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.BAD_REQUEST)
        }
    }

    @Test
    fun create_shouldSetupOwner_for_store_admin() {
        tester.setStoreAdminUser()
        val store = Store("Superstore")

        val actualStore = tester.test_create(store)

        assertThat(actualStore).isNotNull
        assertThat(actualStore.owner).isEqualTo(TestCatalog.OWNER)
    }

    @Test
    fun delete_shouldRemove_for_admin() {
        tester.setAdminUser()

        val store = Store("Superstore")
        store.owner = TestCatalog.OWNER
        tester.test_delete(store)

        assertThat(tester.getEntityManager().find(Store::class.java, store.id)).isNull()
    }

    @Test
    fun delete_shouldRemove_for_store_admin() {
        tester.setStoreAdminUser()

        val store = Store("Superstore")
        store.owner = TestCatalog.OWNER
        tester.test_delete(store)

        assertThat(tester.getEntityManager().find(Store::class.java, store.id)).isNull()
    }

    @Test
    fun delete_shouldThrowForbidden_for_store_admin() {
        try {
            tester.setStoreAdminUser()
            val store = Store("Superstore")
            store.owner = "test@test.org"
            tester.test_delete(store)
            fail("Should have throw an exception")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.FORBIDDEN)
        }
    }

    @Test
    fun modify_shouldThrowForbidden_for_store_admin() {
        try {
            tester.setSAnotherStoreAdminUser()
            val store = Store(1L, "Superstore")
            tester.test_modify(store)
            fail("Should have throw an exception")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.FORBIDDEN)
        }
    }

    @Test
    fun modify_shouldThrowNotFound_for_store_admin() {
        try {
            tester.setStoreAdminUser()
            val store = Store(666L, "Superstore")
            tester.test_modify(store)
            fail("Should have throw an exception")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
        }
    }

    @Test
    fun modify_shouldModify_for_store_admin() {
        tester.setStoreAdminUser()
        val store = Store(1L, "Superstore 2")
        store.owner = TestCatalog.OWNER
        tester.test_modify(store)

        val actual = tester.getEntityManager().find(Store::class.java, store.id)
        assertThat(actual).isNotNull
        assertThat(actual.name).isEqualTo("Superstore 2")
    }

    @Test
    fun modify_shouldModify_for_admin() {
        tester.setAdminUser()
        val store = Store(1L, "Superstore 2")
        store.owner = TestCatalog.OWNER
        tester.test_modify(store)

        val actual = tester.getEntityManager().find(Store::class.java, store.id)
        assertThat(actual).isNotNull
        assertThat(actual.name).isEqualTo("Superstore 2")
    }

    @Test
    fun findCatalog_should_retrieve_catalogs() {
        val catalogs = localService.findCatalogs(tester.getSecurityContext(), 2L, null)
        assertThat(catalogs).isNotEmpty
    }
}
