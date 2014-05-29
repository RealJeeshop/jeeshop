package org.rembx.jeeshop.catalog;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.util.TestCatalog;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ProductResourceTest {

    private ProductResource service;

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
        service = new ProductResource(entityManager,new CatalogItemFinder(entityManager));
    }

    @Test
    public void find_withIdOfAvailableProduct_ShouldReturnExpectedProduct() {
        assertThat(service.find(testCatalog.aProductWithSkus().getId(),null)).isEqualTo(testCatalog.aProductWithSkus());
    }

    @Test
    public void find_withIdOfDisableProduct_ShouldThrowForbiddenException() {
        try{
            service.find(testCatalog.aDisabledProduct().getId(),null);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertEquals(Response.Status.FORBIDDEN,e.getResponse().getStatusInfo());
        }
    }

    @Test
    public void find_withIdOfExpiredProduct_ShouldThrowForbiddenException() {
        try{
            service.find(testCatalog.anExpiredProduct().getId(),null);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertEquals(Response.Status.FORBIDDEN,e.getResponse().getStatusInfo());
        }
    }

    @Test
    public void find_withUnknownProductId_ShouldThrowNotFoundException() {
        try{
            service.find(9999L,null);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertEquals(Response.Status.NOT_FOUND,e.getResponse().getStatusInfo());
        }
    }



}