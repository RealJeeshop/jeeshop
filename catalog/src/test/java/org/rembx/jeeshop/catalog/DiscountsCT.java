package org.rembx.jeeshop.catalog;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Discount;
import org.rembx.jeeshop.catalog.test.TestCatalog;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.Assert.fail;
import static org.rembx.jeeshop.catalog.model.Discount.ApplicableTo.ORDER;
import static org.rembx.jeeshop.catalog.model.Discount.Trigger.AMOUNT;
import static org.rembx.jeeshop.catalog.model.Discount.Trigger.QUANTITY;
import static org.rembx.jeeshop.catalog.model.Discount.Type.DISCOUNT_RATE;
import static org.rembx.jeeshop.catalog.test.Assertions.assertThat;

public class DiscountsCT {

    private Discounts service;

    private TestCatalog testCatalog;
    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @BeforeClass
    public static void beforeClass(){
        entityManagerFactory = Persistence.createEntityManagerFactory(CatalogPersistenceUnit.NAME);
    }

    @Before
    public void setup(){
        testCatalog = TestCatalog.getInstance();
        entityManager = entityManagerFactory.createEntityManager();
        service = new Discounts(entityManager, new CatalogItemFinder(entityManager));
    }

    @Test
    public void find_withIdOfVisibleDiscount_ShouldReturnExpectedDiscount() {
        assertThat(service.find(testCatalog.aVisibleDisount().getId(),null)).isEqualTo(testCatalog.aVisibleDisount());
        assertThat(service.find(testCatalog.aVisibleDisount().getId(),null).isVisible()).isTrue();
    }

    @Test
    public void findAll_shouldReturnNoneEmptyList() {
        assertThat(service.findAll(null,null, null, null, null)).isNotEmpty();
    }

    @Test
    public void findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        List<Discount> discounts = service.findAll(null,0, 1, null, null);
        assertThat(discounts).isNotEmpty();
        assertThat(discounts).hasSize(1);
    }

    @Test
    public void findAll_withIdSearchParam_shouldReturnResultsWithMatchingId() {
        assertThat(service.findAll(testCatalog.aVisibleDisount().getId().toString(),null, null, null, null)).containsExactly(testCatalog.aVisibleDisount());
    }

    @Test
    public void findAll_withNameSearchParam_shouldReturnResultsWithMatchingName() {
        assertThat(service.findAll(testCatalog.aVisibleDisount().getName(),null, null, null, null)).containsExactly(testCatalog.aVisibleDisount());
    }

    @Test
    public void modifyUnknownDiscount_ShouldThrowNotFoundException() {

        Discount detachedDiscountToModify = new Discount(9999L);
        try {
            service.modify(detachedDiscountToModify);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test
    public void countAll(){
        assertThat(service.count(null)).isGreaterThan(0);
    }

    @Test
    public void countAll_withUnknownSearchCriteria(){
        assertThat(service.count("666")).isEqualTo(0);
    }

    @Test
    public void create_shouldPersist(){
        Discount discount = new Discount("discount777", "a discount", ORDER , DISCOUNT_RATE, AMOUNT, null, 0.1, 2.0,  1, true, null, null, false);

        entityManager.getTransaction().begin();
        service.create(discount);
        entityManager.getTransaction().commit();

        assertThat(entityManager.find(Discount.class, discount.getId())).isNotNull();
        entityManager.remove(discount);
    }

    @Test
    public void delete_shouldRemove(){

        entityManager.getTransaction().begin();
        Discount discount = new Discount("discount888", "a discount", ORDER, DISCOUNT_RATE, QUANTITY, null, 0.1,2.0, 1, true, null, null, false);
        entityManager.persist(discount);
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        service.delete(discount.getId());
        entityManager.getTransaction().commit();

        assertThat(entityManager.find(Discount.class, discount.getId())).isNull();
    }

    @Test
    public void delete_NotExistingEntry_shouldThrowNotFoundEx(){

        try {
            entityManager.getTransaction().begin();
            service.delete(666L);
            entityManager.getTransaction().commit();
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test //TODO
    public void findVisible(){

    }
}