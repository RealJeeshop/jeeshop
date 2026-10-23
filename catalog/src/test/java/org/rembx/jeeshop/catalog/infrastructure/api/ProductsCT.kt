package org.rembx.jeeshop.catalog.infrastructure.api

import jakarta.ws.rs.core.Response
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.rembx.jeeshop.catalog.infrastructure.repository.CatalogItemRepositoryImpl
import org.rembx.jeeshop.catalog.domain.Product
import org.rembx.jeeshop.catalog.test.CatalogItemCRUDTester
import org.rembx.jeeshop.catalog.test.TestCatalog
import org.rembx.jeeshop.catalog.test.assertThatSKUsOf
import org.rembx.jeeshop.rest.WebApplicationException
import java.util.Date

class ProductsCT {
    private lateinit var localService: Products
    private lateinit var tester: CatalogItemCRUDTester<Product>

    @BeforeEach
    fun setup() {
        tester = CatalogItemCRUDTester(Product::class.java)
        localService = Products(tester.getEntityManager(), CatalogItemRepositoryImpl(tester.getEntityManager()), null)
        tester.setService(localService)
    }

    @Test
    fun find_withIdOfVisibleProduct_ShouldReturnExpectedProduct() {
        assertThat(localService.find(null, tester.getFixtures().aProductWithSKUs().id, null)).isEqualTo(tester.getFixtures().aProductWithSKUs())
        assertThat(localService.find(null, tester.getFixtures().aProductWithSKUs().id, null).isVisible()).isTrue
    }

    @Test
    fun find_withIdOfDisableProduct_ShouldThrowForbiddenException() {
        try {
            localService.find(null, tester.getFixtures().aDisabledProduct().id, null)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.FORBIDDEN)
        }
    }

    @Test
    fun find_withIdOfExpiredProduct_ShouldThrowForbiddenException() {
        try {
            localService.find(null, tester.getFixtures().anExpiredProduct().id, null)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.FORBIDDEN)
        }
    }

    @Test
    fun find_withUnknownProductId_ShouldThrowNotFoundException() {
        try {
            localService.find(null, 9999L, null)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
        }
    }

    @Test
    fun findSKUs_shouldReturn404ExWhenProductNotFound() {
        try {
            localService.findChildSKUs(null, 9999L, null)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
        }
    }

    @Test
    fun findSKUs_shouldNotReturnExpiredNorDisabledSKUs() {
        val skus = localService.findChildSKUs(null, tester.getFixtures().aProductWithSKUs().id, null)
        assertThat(skus).isNotEmpty
        assertThatSKUsOf(skus).areVisibleSKUsOfAProductWithSKUs()
    }

    @Test
    fun findSKUs_shouldReturnEmptyListWhenNoChildProducts() {
        val skus = localService.findChildSKUs(null, tester.getFixtures().aProductWithoutSKUs().id, null)
        assertThat(skus).isEmpty()
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
        assertThat(localService.findAll(tester.getFixtures().aProductWithoutSKUs().id.toString(), null, null, null, null, null)).containsExactly(tester.getFixtures().aProductWithoutSKUs())
    }

    @Test
    fun findAll_withNameSearchParam_shouldReturnResultsWithMatchingName() {
        assertThat(localService.findAll(tester.getFixtures().aProductWithoutSKUs().name, null, null, null, null, null)).containsExactly(tester.getFixtures().aProductWithoutSKUs())
    }

    @Test
    fun modifyProduct_ShouldModifyProductAttributesAndPreserveSKUsWhenNotProvided() {
        tester.setAdminUser()

        val product = Product(tester.getFixtures().aProductWithSKUs().id, "New name")
        tester.test_modify(product)

        assertThat(product.name).isEqualTo("New name")
        assertThat(product.childSKUs).containsOnlyElementsOf(product.childSKUs)
    }

    @Test
    fun modifyUnknownProduct_ShouldThrowNotFoundException() {
        val detachedProductToModify = Product(9999L, null)
        try {
            tester.setAdminUser()
            localService.modify(tester.getSecurityContext(), detachedProductToModify)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
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
    fun create_shouldPersist_for_admin() {
        tester.setAdminUser()
        val product = Product("name", "description", Date(), Date(), false, TestCatalog.OWNER)

        val actualProduct = tester.test_create(product)

        assertThat(actualProduct).isNotNull
        assertThat(actualProduct.owner).isEqualTo(TestCatalog.OWNER)
    }

    @Test
    fun create_withoutOwner_shouldThrow_BadRequest_for_admin() {
        try {
            tester.setAdminUser()
            val product = Product("name")
            tester.test_create(product)
            fail("Should have thrown an exception")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.BAD_REQUEST)
        }
    }

    @Test
    fun create_shouldPersist_for_store_admin() {
        tester.setAdminUser()
        val product = Product("name", "description", Date(), Date(), false, TestCatalog.OWNER)

        val actualProduct = tester.test_create(product)

        assertThat(actualProduct).isNotNull
        assertThat(actualProduct.owner).isEqualTo(TestCatalog.OWNER)
    }

    @Test
    fun delete_shouldRemove() {
        tester.setStoreAdminUser()
        val product = Product("Test", "", null, null, null, "test@test.com")
        product.owner = TestCatalog.OWNER
        tester.test_delete(product)
        assertThat(tester.getEntityManager().find(Product::class.java, product.id)).isNull()
    }

    @Test
    fun delete_NonManagedEntity_shouldThrow_Forbidden() {
        try {
            tester.setStoreAdminUser()
            val product = Product("Test", "", null, null, null, "test@test.com")
            tester.test_delete(product)
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.FORBIDDEN)
        }
    }

    @Test
    fun delete_NotExistingEntry_shouldThrowNotFoundEx() {
        try {
            tester.setAdminUser()
            localService.delete(tester.getSecurityContext(), 666L)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
        }
    }
}
