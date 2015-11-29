package org.rembx.jeeshop.catalog;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Discount;
import org.rembx.jeeshop.catalog.model.SKU;
import org.rembx.jeeshop.catalog.test.TestCatalog;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.fail;
import static org.rembx.jeeshop.catalog.test.Assertions.assertThat;
import static org.rembx.jeeshop.catalog.test.Assertions.assertThatDiscountsOf;

public class SKUsCT {

    private SKUs service;

    private TestCatalog testCatalog;
    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @BeforeClass
    public static void beforeClass() {
        entityManagerFactory = Persistence.createEntityManagerFactory(CatalogPersistenceUnit.NAME);
    }

    @Before
    public void setup() {
        testCatalog = TestCatalog.getInstance();
        entityManager = entityManagerFactory.createEntityManager();
        service = new SKUs(entityManager, new CatalogItemFinder(entityManager));
    }

    @Test
    public void find_withIdOfVisibleSKU_ShouldReturnExpectedSKU() {
        assertThat(service.find(testCatalog.aVisibleSKU().getId(), null)).isEqualTo(testCatalog.aVisibleSKU());
        assertThat(service.find(testCatalog.aVisibleSKU().getId(), null).isVisible()).isTrue();
    }

    @Test
    public void find_withIdOfDisableSKU_ShouldThrowForbiddenException() {
        try {
            service.find(testCatalog.aDisabledSKU().getId(), null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.FORBIDDEN);
        }
    }

    @Test
    public void find_withIdOfUnAvailableSKU_ShouldReturnUnAvailableSKU() {
        assertThat(service.find(testCatalog.aSKUNotAvailable().getId(), null)).isEqualTo(testCatalog.aSKUNotAvailable());
        assertThat(service.find(testCatalog.aSKUNotAvailable().getId(), null).isAvailable()).isFalse();

    }

    @Test
    public void find_withIdOfExpiredSKU_ShouldThrowForbiddenException() {
        try {
            service.find(testCatalog.anExpiredSKU().getId(), null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.FORBIDDEN);
        }
    }

    @Test
    public void find_withUnknownSKUId_ShouldThrowNotFoundException() {
        try {
            service.find(9999L, null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }


    @Test
    public void findDiscounts_shouldReturn404ExWhenSKUNotFound() {
        try {
            service.findDiscounts(9999L);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test
    public void findDiscounts_shouldNotReturnExpiredNorDisabledDiscounts() {
        List<Discount> discounts = service.findDiscounts(testCatalog.aSKUWithDiscounts().getId());
        assertThat(discounts).isNotEmpty();
        assertThatDiscountsOf(discounts).areVisibleDiscountsOfASKUWithDiscounts();
    }

    @Test
    public void findAll_shouldReturnNoneEmptyList() {
        assertThat(service.findAll(null, null, null, null, null)).isNotEmpty();
    }

    @Test
    public void findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        List<SKU> categories = service.findAll(null, 0, 1, null, null);
        assertThat(categories).isNotEmpty();
        assertThat(categories).hasSize(1);
    }

    @Test
    public void findAll_withIdSearchParam_shouldReturnResultsWithMatchingId() {
        assertThat(service.findAll(testCatalog.aSKUNotAvailable().getId().toString(), null, null, null, null)).containsExactly(testCatalog.aSKUNotAvailable());
    }

    @Test
    public void findAll_withNameSearchParam_shouldReturnResultsWithMatchingName() {
        assertThat(service.findAll(testCatalog.aSKUNotAvailable().getName(), null, null, null, null)).containsExactly(testCatalog.aSKUNotAvailable());
    }

    @Test
    public void modifyUnknownSKU_ShouldThrowNotFoundException() {

        SKU detachedProductToModify = new SKU(9999L, null, null, null, null, null, null, null, null, null);
        try {
            service.modify(detachedProductToModify);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test
    public void countAll() {
        assertThat(service.count(null)).isGreaterThan(0);
    }

    @Test
    public void countAll_withUnknownSearchCriteria() {
        assertThat(service.count("666")).isEqualTo(0);
    }

    @Test
    public void create_shouldPersist() {
        SKU sku = new SKU("name", "description", 1.0, 2, "reference",
                new Date(), new Date(), false, 1);

        entityManager.getTransaction().begin();
        service.create(sku);
        entityManager.getTransaction().commit();

        assertThat(entityManager.find(SKU.class, sku.getId())).isNotNull();
        entityManager.remove(sku);
    }

    @Test
    public void delete_shouldRemove() {

        entityManager.getTransaction().begin();
        SKU sku = new SKU("Test", "", null, null, null, null, null, null, null);
        entityManager.persist(sku);
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        service.delete(sku.getId());
        entityManager.getTransaction().commit();

        assertThat(entityManager.find(SKU.class, sku.getId())).isNull();
    }

    @Test
    public void delete_NotExistingEntry_shouldThrowNotFoundEx() {

        try {
            entityManager.getTransaction().begin();
            service.delete(666L);
            entityManager.getTransaction().commit();
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }
}