package org.rembx.jeeshop.catalog.infrastructure.api

import jakarta.ws.rs.core.Response
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.rembx.jeeshop.catalog.infrastructure.repository.CatalogItemRepositoryImpl
import org.rembx.jeeshop.catalog.domain.SKU
import org.rembx.jeeshop.catalog.test.CatalogItemCRUDTester
import org.rembx.jeeshop.catalog.test.TestCatalog
import org.rembx.jeeshop.catalog.test.assertThat
import org.rembx.jeeshop.catalog.test.assertThatDiscountsOf
import org.rembx.jeeshop.rest.WebApplicationException
import java.util.Date

class SKUsCT {
    private lateinit var localService: SKUs
    private lateinit var tester: CatalogItemCRUDTester<SKU>

    @BeforeEach
    fun setup() {
        tester = CatalogItemCRUDTester(SKU::class.java)
        localService = SKUs(tester.getEntityManager(), CatalogItemRepositoryImpl(tester.getEntityManager()), null)
        tester.setService(localService)
    }

    @Test
    fun find_withIdOfVisibleSKU_ShouldReturnExpectedSKU() {
        val catalogItem = localService.find(tester.getSecurityContext(), tester.getFixtures().aVisibleSKU().id, null)
        assertThat(catalogItem).isEqualTo(tester.getFixtures().aVisibleSKU())
        assertThat(catalogItem.isVisible()).isTrue
    }

    @Test
    fun find_withIdOfDisableSKU_ShouldThrowForbiddenException() {
        try {
            localService.find(tester.getSecurityContext(), tester.getFixtures().aDisabledSKU().id, null)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.FORBIDDEN)
        }
    }

    @Test
    fun find_withIdOfUnAvailableSKU_ShouldReturnUnAvailableSKU() {
        assertThat(localService.find(tester.getSecurityContext(), tester.getFixtures().aSKUNotAvailable().id, null)).isEqualTo(tester.getFixtures().aSKUNotAvailable())
        assertThat(localService.find(tester.getSecurityContext(), tester.getFixtures().aSKUNotAvailable().id, null).isAvailable()).isFalse
    }

    @Test
    fun find_withIdOfExpiredSKU_ShouldThrowForbiddenException() {
        try {
            localService.find(tester.getSecurityContext(), tester.getFixtures().anExpiredSKU().id, null)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.FORBIDDEN)
        }
    }

    @Test
    fun find_withUnknownSKUId_ShouldThrowNotFoundException() {
        try {
            localService.find(tester.getSecurityContext(), 9999L, null)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
        }
    }

    @Test
    fun findDiscounts_shouldReturn404ExWhenSKUNotFound() {
        try {
            localService.findDiscounts(tester.getSecurityContext(), 9999L)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
        }
    }

    @Test
    fun findDiscounts_shouldNotReturnExpiredNorDisabledDiscounts() {
        val discounts = localService.findDiscounts(tester.getSecurityContext(), tester.getFixtures().aSKUWithDiscounts().id)
        assertThat(discounts).isNotEmpty
        assertThatDiscountsOf(discounts).areVisibleDiscountsOfASKUWithDiscounts()
    }

    @Test
    fun findAll_shouldReturnNoneEmptyList() {
        assertThat(localService.findAll(null, null, null, null, null, null)).isNotEmpty
    }

    @Test
    fun findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        val categories = localService.findAll(null, 0, 1, null, null, null)
        assertThat(categories).isNotEmpty
        assertThat(categories).hasSize(1)
    }

    @Test
    fun findAll_withIdSearchParam_shouldReturnResultsWithMatchingId() {
        assertThat(localService.findAll(tester.getFixtures().aSKUNotAvailable().id.toString(), null, null, null, null, null)).containsExactly(tester.getFixtures().aSKUNotAvailable())
    }

    @Test
    fun findAll_withNameSearchParam_shouldReturnResultsWithMatchingName() {
        assertThat(localService.findAll(tester.getFixtures().aSKUNotAvailable().name, null, null, null, null, null)).containsExactly(tester.getFixtures().aSKUNotAvailable())
    }

    @Test
    fun modifyUnknownSKU_ShouldThrowNotFoundException() {
        val detachedProductToModify = SKU(9999L, null, null, null, null, null, null, null, null, null)
        try {
            localService.modify(tester.getSecurityContext(), detachedProductToModify)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
        }
    }

    @Test
    fun modify_ShouldThrowForbiddenException_for_store_admin() {
        tester.setSAnotherStoreAdminUser()
        val sku = SKU(tester.getFixtures().aVisibleSKU().id, "New name")
        try {
            tester.test_modify(sku)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.FORBIDDEN)
        }
    }

    @Test
    fun modify_ShouldModify_for_store_admin() {
        tester.setStoreAdminUser()
        val sku = SKU(tester.getFixtures().aVisibleSKU().id, "New name")
        tester.test_modify(sku)

        Assertions.assertThat(sku.name).isEqualTo("New name")
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
    fun create_shouldPersistAndAttachOwner_for_admin() {
        tester.setAdminUser()

        val sku = SKU("name", "description", 1.0, 2, "reference", Date(), Date(), false, 1, "test@test.com")

        val createdSKU = tester.test_create(sku)
        assertThat(createdSKU).isNotNull
        assertThat(createdSKU.owner).isEqualTo("test@test.com")
    }

    @Test
    fun create_shouldPersistAndAttachOwner_for_store_admin() {
        tester.setStoreAdminUser()

        val sku = SKU("name", "description", 1.0, 2, "reference", Date(), Date(), false, 1, null)

        val createdSKU = tester.test_create(sku)
        assertThat(createdSKU).isNotNull
        assertThat(createdSKU.owner).isEqualTo(TestCatalog.OWNER)
    }

    @Test
    fun create_shouldThrow_BadRequest_for_Admin() {
        try {
            tester.setAdminUser()
            val sku = SKU("Test", "")
            tester.test_create(sku)
            fail("should have throw an exception")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.BAD_REQUEST)
        }
    }

    @Test
    fun delete_shouldRemove_for_admin() {
        tester.setAdminUser()
        val sku = SKU("Test", "")
        sku.owner = "an@owner.fr"
        tester.test_delete(sku)
        Assertions.assertThat(
            tester.getEntityManager().find(SKU::class.java, sku.id),
        ).isNull()
    }

    @Test
    fun delete_shouldRemove_for_store_Admin() {
        tester.setStoreAdminUser()
        val sku = SKU("Test", "")
        sku.owner = TestCatalog.OWNER
        tester.test_delete(sku)
        Assertions.assertThat(
            tester.getEntityManager().find(SKU::class.java, sku.id),
        ).isNull()
    }

    @Test
    fun delete_shouldThrow_Forbidden_for_store_Admin() {
        try {
            tester.setStoreAdminUser()
            val sku = SKU("Test", "")
            sku.owner = "test@test.org"
            tester.test_delete(sku)
            fail("should have throw an exception")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.FORBIDDEN)
        }
    }

    @Test
    fun delete_NotExistingEntry_shouldThrowNotFoundEx() {
        try {
            tester.getEntityManager().transaction.begin()
            localService.delete(tester.getSecurityContext(), 666L)
            tester.getEntityManager().transaction.commit()
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
        }
    }
}
