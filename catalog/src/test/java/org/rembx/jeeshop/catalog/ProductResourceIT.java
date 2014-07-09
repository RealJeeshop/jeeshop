package org.rembx.jeeshop.catalog;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Product;
import org.rembx.jeeshop.catalog.model.SKU;
import org.rembx.jeeshop.catalog.test.TestCatalog;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.rembx.jeeshop.catalog.test.Assertions.assertThatSKUsOf;

public class ProductResourceIT {

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
    public void find_withIdOfVisibleProduct_ShouldReturnExpectedProduct() {
        assertThat(service.find(testCatalog.aProductWithSKUs().getId(),null)).isEqualTo(testCatalog.aProductWithSKUs());
        assertThat(service.find(testCatalog.aProductWithSKUs().getId(),null).isVisible()).isTrue();
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


    @Test
    public void findSKUs_shouldReturn404ExWhenProductNotFound() {
        try{
            service.findChildSKUs(9999L, null);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertEquals(Response.Status.NOT_FOUND,e.getResponse().getStatusInfo());
        }
    }

    @Test
    public void findSKUs_shouldNotReturnExpiredNorDisabledSKUs() {
        List<SKU> skus = service.findChildSKUs(testCatalog.aProductWithSKUs().getId(), null);
        assertNotNull(skus);
        assertThatSKUsOf(skus).areVisibleSKUsOfAProductWithSKUs();
    }

    @Test
    public void findSKUs_shouldReturnEmptyListWhenNoChildProducts() {
        List<SKU> skus = service.findChildSKUs(testCatalog.aProductWithoutSKUs().getId(), null);
        assertThat(skus).isEmpty();
    }

    @Test
    public void findAll_shouldReturnNoneEmptyList() {
        assertThat(service.findAll(null, null)).isNotEmpty();
    }

    @Test
    public void findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        List<Product> categories = service.findAll(0, 1);
        assertThat(categories).isNotEmpty();
        assertThat(categories).hasSize(1);
    }

    @Test
    public void modifyProduct_ShouldModifyProductAttributesAndPreserveSKUsWhenNotProvided() {
        Product product = service.find(testCatalog.aProductWithSKUs().getId(), null);

        Product detachedProductToModify = new Product(testCatalog.aProductWithSKUs().getId(), product.getName(), product.getDescription(), product.getStartDate(), product.getEndDate(), product.isDisabled());

        service.modify(detachedProductToModify);

        assertThat(product.getChildSKUs()).containsExactly(product.getChildSKUs().toArray());

    }

    @Test
    public void modifyUnknownProduct_ShouldThrowNotFoundException() {

        Product detachedProductToModify = new Product(9999L,null,null,null,null,null);
        try {
            service.modify(detachedProductToModify);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertThat(e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode());
        }
    }

}