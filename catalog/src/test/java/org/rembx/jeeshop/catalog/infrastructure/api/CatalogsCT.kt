package org.rembx.jeeshop.catalog.infrastructure.api

import jakarta.ws.rs.core.Response
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.rembx.jeeshop.catalog.infrastructure.repository.CatalogItemRepositoryImpl
import org.rembx.jeeshop.catalog.domain.Catalog
import org.rembx.jeeshop.catalog.test.CatalogItemCRUDTester
import org.rembx.jeeshop.catalog.test.TestCatalog
import org.rembx.jeeshop.catalog.test.assertThatCategoriesOf
import org.rembx.jeeshop.rest.WebApplicationException
import org.rembx.jeeshop.role.JeeshopRoles

class CatalogsCT {
    private lateinit var localService: Catalogs
    private lateinit var tester: CatalogItemCRUDTester<Catalog>

    @BeforeEach
    fun setupClass() {
        tester = CatalogItemCRUDTester(Catalog::class.java)
        localService = Catalogs(tester.getEntityManager(), CatalogItemRepositoryImpl(tester.getEntityManager()), null)
        tester.setService(localService)
    }

    @Test
    fun findCategories_shouldReturn404ExWhenCatalogNotFound() {
        try {
            localService.findCategories(null, 9999L, null)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
        }
    }

    @Test
    fun findCategories_shouldReturnEmptyListWhenCatalogIsEmpty() {
        val categories = localService.findCategories(null, tester.getFixtures().getEmptyCatalogId(), null)
        assertThat(categories).isEmpty()
    }

    @Test
    fun findCategories_shouldNotReturnExpiredNorDisabledRootCategories() {
        `when`(tester.getSecurityContext().isUserInRole(JeeshopRoles.ADMIN)).thenReturn(false)
        `when`(tester.getSecurityContext().isUserInRole(JeeshopRoles.STORE_ADMIN)).thenReturn(false)
        val categories = localService.findCategories(tester.getSecurityContext(), tester.getFixtures().getCatalogId(), null)
        assertThatCategoriesOf(categories).areVisibleRootCategories()
    }

    @Test
    fun findAll_shouldReturnNoneEmptyList() {
        assertThat(localService.findAll(null, null, null, null, null, null)).isNotEmpty
    }

    @Test
    fun findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        val catalogs = localService.findAll(null, 0, 1, null, null, null)
        assertThat(catalogs).isNotEmpty
        assertThat(catalogs).hasSize(1)
    }

    @Test
    fun findAll_withIdSearchParam_shouldReturnResultsWithMatchingId() {
        assertThat(localService.findAll(tester.getFixtures().getCatalogId().toString(), null, null, null, null, null)).containsExactly(TestCatalog.getCatalog())
    }

    @Test
    fun findAll_withNameSearchParam_shouldReturnResultsWithMatchingName() {
        assertThat(localService.findAll(TestCatalog.getCatalog().name, null, null, null, null, null)).containsExactly(TestCatalog.getCatalog())
    }

    @Test
    fun find_withIdOfVisibleCatalog_ShouldReturnExpectedCatalog() {
        tester.setStoreAdminUser()

        val catalog = localService.find(tester.getSecurityContext(), tester.getFixtures().getCatalogId(), null)
        assertThat(catalog).isNotNull
        assertThat(catalog.isVisible()).isTrue
    }

    @Test
    fun modifyCatalog_ShouldModifyCatalogAttributesAndPreserveRootCategoriesWhenNotProvided() {
        tester.setStoreAdminUser()

        val detachedCatalogToModify = Catalog(2L, "New name")
        tester.test_modify(detachedCatalogToModify)

        assertThat(detachedCatalogToModify.name).isEqualTo("New name")
        assertThat(detachedCatalogToModify.rootCategories).isNotEmpty
    }

    @Test
    fun modifyUnknownCatalog_ShouldThrowNotFoundException() {
        tester.setStoreAdminUser()
        val detachedCatalogToModify = Catalog(9999L, null)
        try {
            tester.test_modify(detachedCatalogToModify)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
        }
    }

    @Test
    fun modifyNonManagedCatalog_ShouldThrowForbiddenException() {
        tester.setSAnotherStoreAdminUser()
        val detachedCatalogToModify = Catalog(1L, "name")

        try {
            tester.test_modify(detachedCatalogToModify)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.FORBIDDEN)
        }
    }

    @Test
    fun countAll() {
        assertThat(localService.count(null)).isGreaterThan(0)
    }

    @Test
    fun countAll_withUnknownSearchCriteria() {
        assertThat(localService.count("666")).isEqualTo(0)
    }

    @Test
    fun create_shouldSetupOwner_for_admin() {
        tester.setAdminUser()
        val catalog = Catalog("Catalog")
        catalog.owner = TestCatalog.OWNER

        val actualCatalog = tester.test_create(catalog)

        assertThat(actualCatalog).isNotNull
        assertThat(actualCatalog.owner).isEqualTo(TestCatalog.OWNER)
    }

    @Test
    fun create_shouldThrowBadRequest_whenOwnerIsNull_for_admin() {
        tester.setAdminUser()
        val catalog = Catalog("Catalog")

        try {
            tester.test_create(catalog)
            fail("should have thrown an exception")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.BAD_REQUEST)
        }
    }

    @Test
    fun create_shouldSetupOwner_for_store_admin() {
        tester.setStoreAdminUser()
        val catalog = Catalog("Catalog")

        val actualCatalog = tester.test_create(catalog)

        assertThat(actualCatalog).isNotNull
        assertThat(actualCatalog.owner).isEqualTo(TestCatalog.OWNER)
    }

    @Test
    fun delete_shouldRemove() {
        tester.setStoreAdminUser()

        val catalog = Catalog("Test Catalog")
        catalog.owner = TestCatalog.OWNER

        tester.test_delete(catalog)

        assertThat(tester.getEntityManager().find(Catalog::class.java, catalog.id)).isNull()
    }

    @Test
    fun delete_shouldThrowForbidden_for_store_admin() {
        try {
            tester.setStoreAdminUser()
            val catalog = Catalog("catalog")
            catalog.owner = "test@test.org"
            tester.test_delete(catalog)
            fail("Should have throw an exception")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.FORBIDDEN)
        }
    }

    @Test
    fun delete_NotExistingEntry_shouldThrowNotFoundEx() {
        try {
            tester.setStoreAdminUser()
            localService.delete(tester.getSecurityContext(), 666L)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
        }
    }
}
