package org.rembx.jeeshop.catalog;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Discount;
import org.rembx.jeeshop.catalog.test.TestCatalog;
import org.rembx.jeeshop.catalog.util.CatalogItemResourceUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.Assert.*;
import static org.rembx.jeeshop.catalog.test.Assertions.assertThat;
import static org.rembx.jeeshop.catalog.test.Assertions.assertThatDiscountsOf;

public class SKUResourceIT {

    private SKUResource service;

    private TestCatalog testCatalog;
    private static EntityManagerFactory entityManagerFactory;

    @BeforeClass
    public static void beforeClass(){
        entityManagerFactory = Persistence.createEntityManagerFactory(CatalogPersistenceUnit.NAME);
    }

    @Before
    public void setup(){
        testCatalog = TestCatalog.getInstance();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        service = new SKUResource(entityManager, new CatalogItemFinder(entityManager), new CatalogItemResourceUtil());
    }

    @Test
    public void find_withIdOfVisibleSKU_ShouldReturnExpectedSKU() {
        assertThat(service.find(testCatalog.aVisibleSKU().getId(),null)).isEqualTo(testCatalog.aVisibleSKU());
        assertThat(service.find(testCatalog.aVisibleSKU().getId(),null).isVisible()).isTrue();
    }

    @Test
    public void find_withIdOfDisableSKU_ShouldThrowForbiddenException() {
        try{
            service.find(testCatalog.aDisabledSKU().getId(),null);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertEquals(Response.Status.FORBIDDEN,e.getResponse().getStatusInfo());
        }
    }

    @Test
    public void find_withIdOfUnAvailableSKU_ShouldReturnUnAvailableSKU() {
        assertThat(service.find(testCatalog.aSKUNotAvailable().getId(),null)).isEqualTo(testCatalog.aSKUNotAvailable());
        assertThat(service.find(testCatalog.aSKUNotAvailable().getId(),null).isAvailable()).isFalse();

    }

    @Test
    public void find_withIdOfExpiredSKU_ShouldThrowForbiddenException() {
        try{
            service.find(testCatalog.anExpiredSKU().getId(),null);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertEquals(Response.Status.FORBIDDEN,e.getResponse().getStatusInfo());
        }
    }

    @Test
    public void find_withUnknownSKUId_ShouldThrowNotFoundException() {
        try{
            service.find(9999L,null);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertEquals(Response.Status.NOT_FOUND,e.getResponse().getStatusInfo());
        }
    }


    @Test
    public void findDiscounts_shouldReturn404ExWhenSKUNotFound() {
        try{
            service.findDiscounts(9999L);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertEquals(Response.Status.NOT_FOUND,e.getResponse().getStatusInfo());
        }
    }

    @Test
    public void findDiscounts_shouldNotReturnExpiredNorDisabledDiscounts() {
        List<Discount> discounts = service.findDiscounts(testCatalog.aSKUWithDiscounts().getId());
        assertNotNull(discounts);
        assertThatDiscountsOf(discounts).areVisibleDiscountsOfASKUWithDiscounts();
    }
}