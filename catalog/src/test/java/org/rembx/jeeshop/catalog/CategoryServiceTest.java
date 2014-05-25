package org.rembx.jeeshop.catalog;

import org.junit.Before;
import org.junit.Test;
import org.rembx.jeeshop.catalog.model.Category;
import org.rembx.jeeshop.catalog.model.Product;
import org.rembx.jeeshop.catalog.util.TestCatalog;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.rembx.jeeshop.catalog.util.Assertions.assertThatCategoriesOf;
import static org.rembx.jeeshop.catalog.util.Assertions.assertThatProductsOf;

public class CategoryServiceTest {
    private CategoryService service;

    private TestCatalog testCatalog;

    @Before
    public void setup(){
        testCatalog = TestCatalog.getInstance();
        service = new CategoryService(testCatalog.getEntityManager());
    }

    @Test
    public void find_withIdOfAvailableCategory_ShouldReturnExpectedCategory() {
        assertThat(service.find(testCatalog.aCategoryWithProducts().getId())).isEqualTo(testCatalog.aCategoryWithProducts());
    }

    @Test
    public void find_withIdOfDisableCategory_ShouldThrowForbiddenException() {
        try{
            service.find(testCatalog.aDisableCategory().getId());
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertEquals(Response.Status.FORBIDDEN,e.getResponse().getStatusInfo());
        }
    }

    @Test
    public void find_withIdOfExpiredCategory_ShouldThrowForbiddenException() {
        try{
            service.find(testCatalog.anExpiredCategory().getId());
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertEquals(Response.Status.FORBIDDEN,e.getResponse().getStatusInfo());
        }
    }

    @Test
    public void find_withUnknownCategoryId_ShouldThrowNotFoundException() {
        try{
            service.find(9999L);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertEquals(Response.Status.NOT_FOUND,e.getResponse().getStatusInfo());
        }
    }

    @Test
    public void findCategories_shouldReturn404ExWhenCategoryNotFound() {
        try{
            service.findCategories(9999L);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertEquals(Response.Status.NOT_FOUND,e.getResponse().getStatusInfo());
        }
    }

    @Test
    public void findCategories_shouldNotReturnExpiredNorDisabledCategories() {
        List<Category> categories = service.findCategories(testCatalog.aRootCategoryWithChildCategories().getId());
        assertNotNull(categories);
        assertThatCategoriesOf(categories).areVisibleChildCategoriesOfARootCategoryWithChildCategories();
    }


    @Test
    public void findProducts_shouldReturn404ExWhenCategoryNotFound() {
        try{
            service.findProducts(9999L);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertEquals(Response.Status.NOT_FOUND,e.getResponse().getStatusInfo());
        }
    }

    @Test
    public void findProducts_shouldNotReturnExpiredNorDisabledProducts() {
        List<Product> products = service.findProducts(testCatalog.aCategoryWithProducts().getId());
        assertNotNull(products);
        assertThatProductsOf(products).areVisibleProductsOfAChildCategoryWithProducts();
    }

}