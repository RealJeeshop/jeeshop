package org.rembx.jeeshop.catalog.infrastructure.api

import jakarta.ws.rs.core.Response
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.rembx.jeeshop.catalog.infrastructure.repository.CatalogItemRepositoryImpl
import org.rembx.jeeshop.catalog.infrastructure.repository.DiscountRepositoryImpl
import org.rembx.jeeshop.catalog.domain.Discount
import org.rembx.jeeshop.catalog.test.CatalogItemCRUDTester
import org.rembx.jeeshop.catalog.test.TestCatalog
import org.rembx.jeeshop.catalog.test.assertThat
import org.rembx.jeeshop.rest.WebApplicationException

class DiscountsCT {
    private lateinit var localService: Discounts
    private lateinit var tester: CatalogItemCRUDTester<Discount>

    @BeforeEach
    fun setup() {
        tester = CatalogItemCRUDTester(Discount::class.java)
        localService = Discounts(
            tester.getEntityManager(),
            CatalogItemRepositoryImpl(tester.getEntityManager()),
            null,
            DiscountRepositoryImpl(tester.getEntityManager())
        )
        tester.setService(localService)
    }

    @Test
    fun find_withIdOfVisibleDiscount_ShouldReturnExpectedDiscount() {
        val catalogItem = localService.find(tester.getSecurityContext(), tester.getFixtures().aVisibleDisount().id, null)

        assertThat(catalogItem).isEqualTo(tester.getFixtures().aVisibleDisount())
        assertThat(catalogItem.isVisible()).isTrue
    }

    @Test
    fun findAll_shouldReturnNoneEmptyList() {
        assertThat(localService.findAll(null, null, null, null, null, null)).isNotEmpty
    }

    @Test
    fun findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        val discounts = localService.findAll(null, 0, 1, null, null, null)
        assertThat(discounts).isNotEmpty
        assertThat(discounts).hasSize(1)
    }

    @Test
    fun findAll_withIdSearchParam_shouldReturnResultsWithMatchingId() {
        assertThat(localService.findAll(tester.getFixtures().aVisibleDisount().id.toString(), null, null, null, null, null)).containsExactly(tester.getFixtures().aVisibleDisount())
    }

    @Test
    fun findAll_withNameSearchParam_shouldReturnResultsWithMatchingName() {
        assertThat(localService.findAll(tester.getFixtures().aVisibleDisount().name, null, null, null, null, null)).containsExactly(tester.getFixtures().aVisibleDisount())
    }

    @Test
    fun modifyUnknownDiscount_ShouldThrowNotFoundException() {
        val detachedDiscountToModify = Discount(9999L)
        try {
            localService.modify(tester.getSecurityContext(), detachedDiscountToModify)
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
    fun create_shouldPersist_for_admin_user() {
        tester.setAdminUser()
        val discount = Discount("discount777", "a discount", Discount.ApplicableTo.ORDER, Discount.Type.DISCOUNT_RATE, Discount.Trigger.AMOUNT, null, 0.1, 2.0, 1, true, null, null, false, "test@test.com")

        val createdDiscount = tester.test_create(discount)

        assertThat(createdDiscount).isNotNull
        assertThat(createdDiscount.owner).isEqualTo("test@test.com")
    }

    @Test
    fun delete_shouldRemove_for_admin() {
        tester.setAdminUser()
        val discount = Discount("discount888", "a discount", Discount.ApplicableTo.ORDER, Discount.Type.DISCOUNT_RATE, Discount.Trigger.QUANTITY, null, 0.1, 2.0, 1, true, null, null, false, "test@test.com")
        tester.test_delete(discount)
        Assertions.assertThat(
            tester.getEntityManager().find(Discount::class.java, discount.id),
        ).isNull()
    }

    @Test
    fun delete_shouldRemove_for_store_admin() {
        tester.setStoreAdminUser()
        val discount = Discount(
            "discount888",
            "a discount",
            Discount.ApplicableTo.ORDER,
            Discount.Type.DISCOUNT_RATE,
            Discount.Trigger.QUANTITY,
            null,
            0.1,
            2.0,
            1,
            true,
            null,
            null,
            false,
            TestCatalog.OWNER
        )

        tester.test_delete(discount)
        Assertions.assertThat(
            tester.getEntityManager().find(Discount::class.java, discount.id),
        ).isNull()
    }

    @Test
    fun delete_shouldThrowForbiddenException_for_store_admin() {
        try {
            tester.setStoreAdminUser()
            val discount = Discount(
                "discount888",
                "a discount",
                Discount.ApplicableTo.ORDER,
                Discount.Type.DISCOUNT_RATE,
                Discount.Trigger.QUANTITY,
                null,
                0.1,
                2.0,
                1,
                true,
                null,
                null,
                false,
                "test@test.com"
            )

            tester.test_delete(discount)
            fail("Should have throw an exception")
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

    @Test
    fun findVisible() {
    }
}
