package org.rembx.jeeshop.catalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rembx.jeeshop.catalog.model.Discount;
import org.rembx.jeeshop.catalog.test.CatalogItemCRUDTester;
import org.rembx.jeeshop.catalog.test.TestCatalog;
import org.rembx.jeeshop.rest.WebApplicationException;

import javax.ws.rs.core.Response;
import java.util.List;

import static org.assertj.core.api.Assertions.fail;
import static org.rembx.jeeshop.catalog.model.Discount.ApplicableTo.ORDER;
import static org.rembx.jeeshop.catalog.model.Discount.Trigger.AMOUNT;
import static org.rembx.jeeshop.catalog.model.Discount.Trigger.QUANTITY;
import static org.rembx.jeeshop.catalog.model.Discount.Type.DISCOUNT_RATE;
import static org.rembx.jeeshop.catalog.test.Assertions.assertThat;

public class DiscountsCT {

    private Discounts localService;
    private CatalogItemCRUDTester<Discount> tester;

    @BeforeEach
    public void setup() {
        this.tester = new CatalogItemCRUDTester<>(Discount.class);
        localService = new Discounts(tester.getEntityManager(), new CatalogItemFinder(tester.getEntityManager()), null, new DiscountFinder(tester.getEntityManager()));
        this.tester.setService(this.localService);
    }

    @Test
    public void find_withIdOfVisibleDiscount_ShouldReturnExpectedDiscount() {
        Discount catalogItem = localService.find(tester.getSecurityContext(), tester.getFixtures().aVisibleDisount().getId(), null);

        assertThat(catalogItem).isEqualTo(tester.getFixtures().aVisibleDisount());
        assertThat(catalogItem.isVisible()).isTrue();
    }

    @Test
    public void findAll_shouldReturnNoneEmptyList() {
        assertThat(localService.findAll(null, null, null, null, null, null)).isNotEmpty();
    }

    @Test
    public void findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        List<Discount> discounts = localService.findAll(null, 0, 1, null, null, null);
        assertThat(discounts).isNotEmpty();
        assertThat(discounts).hasSize(1);
    }

    @Test
    public void findAll_withIdSearchParam_shouldReturnResultsWithMatchingId() {
        assertThat(localService.findAll(tester.getFixtures().aVisibleDisount().getId().toString(), null, null, null, null, null)).containsExactly(tester.getFixtures().aVisibleDisount());
    }

    @Test
    public void findAll_withNameSearchParam_shouldReturnResultsWithMatchingName() {
        assertThat(localService.findAll(tester.getFixtures().aVisibleDisount().getName(), null, null, null, null, null)).containsExactly(tester.getFixtures().aVisibleDisount());
    }

    @Test
    public void modifyUnknownDiscount_ShouldThrowNotFoundException() {

        Discount detachedDiscountToModify = new Discount(9999L);
        try {
            localService.modify(tester.getSecurityContext(), detachedDiscountToModify);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test
    public void countAll() {
        assertThat(localService.count(null)).isGreaterThan(0);
    }

    @Test
    public void countAll_withUnknownSearchCriteria() {
        assertThat(localService.count("666")).isEqualTo(0);
    }

    @Test
    public void create_shouldPersist_for_admin_user() {

        tester.setAdminUser();
        Discount discount = new Discount("discount777", "a discount", ORDER, DISCOUNT_RATE, AMOUNT, null, 0.1, 2.0, 1, true, null, null, false, "test@test.com");

        Discount createdDiscount = tester.test_create(discount);

        assertThat(createdDiscount).isNotNull();
        assertThat(createdDiscount.getOwner()).isEqualTo("test@test.com");
    }

    @Test
    public void delete_shouldRemove_for_admin() {

        tester.setAdminUser();
        Discount discount = new Discount("discount888", "a discount", ORDER, DISCOUNT_RATE, QUANTITY, null, 0.1, 2.0, 1, true, null, null, false, "test@test.com");
        tester.test_delete(discount);
        assertThat(tester.getEntityManager().find(Discount.class, discount.getId())).isNull();
    }

    @Test
    public void delete_shouldRemove_for_store_admin() {

        tester.setStoreAdminUser();
        Discount discount = new Discount("discount888", "a discount",
                ORDER, DISCOUNT_RATE, QUANTITY, null, 0.1,
                2.0, 1, true, null,
                null, false, TestCatalog.OWNER);

        tester.test_delete(discount);
        assertThat(tester.getEntityManager().find(Discount.class, discount.getId())).isNull();
    }

    @Test
    public void delete_shouldThrowForbiddenException_for_store_admin() {

        try {
            tester.setStoreAdminUser();
            Discount discount = new Discount("discount888", "a discount",
                    ORDER, DISCOUNT_RATE, QUANTITY, null, 0.1,
                    2.0, 1, true, null,
                    null, false, "test@test.com");

            tester.test_delete(discount);
            fail("Should have throw an exception");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.FORBIDDEN);
        }
    }

    @Test
    public void delete_NotExistingEntry_shouldThrowNotFoundEx() {

        try {
            tester.setAdminUser();
            localService.delete(tester.getSecurityContext(), 666L);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test //TODO
    public void findVisible() {

    }
}