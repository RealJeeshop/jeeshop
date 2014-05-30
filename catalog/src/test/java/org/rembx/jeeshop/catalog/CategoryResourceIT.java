package org.rembx.jeeshop.catalog;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Category;
import org.rembx.jeeshop.catalog.model.Product;
import org.rembx.jeeshop.catalog.util.CatalogItemResourceUtil;
import org.rembx.jeeshop.catalog.test.PresentationTexts;
import org.rembx.jeeshop.catalog.test.TestCatalog;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.rembx.jeeshop.catalog.test.Assertions.assertThat;
import static org.rembx.jeeshop.catalog.test.Assertions.assertThatCategoriesOf;
import static org.rembx.jeeshop.catalog.test.Assertions.assertThatProductsOf;

public class CategoryResourceIT {
    private CategoryResource service;

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
        service = new CategoryResource(entityManager, new CatalogItemFinder(entityManager),new CatalogItemResourceUtil());
    }

    @Test
    public void find_withIdOfVisibleCategory_ShouldReturnExpectedCategory() {
        assertThat(service.find(testCatalog.aCategoryWithProducts().getId(),null)).isEqualTo(testCatalog.aCategoryWithProducts());
        assertThat(service.find(testCatalog.aCategoryWithProducts().getId(),null).isVisible()).isTrue();

    }

    @Test
    public void find_withIdOfDisableCategory_ShouldThrowForbiddenException() {
        try{
            service.find(testCatalog.aDisabledCategory().getId(),null);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertEquals(Response.Status.FORBIDDEN,e.getResponse().getStatusInfo());
        }
    }

    @Test
    public void find_withIdOfExpiredCategory_ShouldThrowForbiddenException() {
        try{
            service.find(testCatalog.anExpiredCategory().getId(),null);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertEquals(Response.Status.FORBIDDEN,e.getResponse().getStatusInfo());
        }
    }

    @Test
    public void find_idOfCategoryWithPresentation_ShouldReturnExpectedPresentation(){
        assertThat(service.find(testCatalog.aCategoryWithPresentation().getId(), Locale.UK.toString())).hasLocalizedPresentationShortDescription(Locale.UK.toString(), PresentationTexts.TEXT_2000);
    }

    @Test
    public void find_idOfCategoryWithPresentation_WithNoLocaleSpecifiedShouldReturnFallbackLocalePresentation(){
        assertThat(service.find(testCatalog.aCategoryWithPresentation().getId(), null)).hasLocalizedPresentationShortDescription(Locale.US.toString(), PresentationTexts.TEXT_2000);
    }

    @Test
    public void find_idOfCategoryWithPresentation_WithNotSupportedLocaleSpecifiedShouldReturnFallbackLocalePresentation(){
        assertThat(service.find(testCatalog.aCategoryWithPresentation().getId(), "it_IT")).hasLocalizedPresentationShortDescription(Locale.US.toString(), PresentationTexts.TEXT_2000);
    }

    @Test
    public void find_withUnknownCategoryId_ShouldThrowNotFoundException() {
        try{
            service.find(9999L,null);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertEquals(Response.Status.NOT_FOUND,e.getResponse().getStatusInfo());
        }
    }

    @Test
    public void findCategories_shouldReturn404ExWhenCategoryNotFound() {
        try{
            service.findCategories(9999L,null);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertEquals(Response.Status.NOT_FOUND,e.getResponse().getStatusInfo());
        }
    }

    @Test
    public void findCategories_shouldNotReturnExpiredNorDisabledCategories() {
        List<Category> categories = service.findCategories(testCatalog.aRootCategoryWithChildCategories().getId(),null);
        assertNotNull(categories);
        assertThatCategoriesOf(categories).areVisibleChildCategoriesOfARootCategoryWithChildCategories();
    }

    @Test
    public void findCategories_shouldReturnEmptyListWhenNoChildCategories() {
        List<Category> categories = service.findCategories(testCatalog.aCategoryWithProducts().getId(),null);
        assertThat(categories).isEmpty();
    }

    @Test
    public void findProducts_shouldReturn404ExWhenCategoryNotFound() {
        try{
            service.findProducts(9999L,null);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertEquals(Response.Status.NOT_FOUND,e.getResponse().getStatusInfo());
        }
    }

    @Test
    public void findProducts_shouldNotReturnExpiredNorDisabledProducts() {
        List<Product> products = service.findProducts(testCatalog.aCategoryWithProducts().getId(),null);
        assertNotNull(products);
        assertThatProductsOf(products).areVisibleProductsOfAChildCategoryWithProducts();
    }

    @Test
    public void findProducts_shouldReturnEmptyListWhenNoChildProducts() {
        List<Product> products = service.findProducts(testCatalog.aCategoryWithoutProducts().getId(),null);
        assertThat(products).isEmpty();
    }

}