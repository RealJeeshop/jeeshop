package org.rembx.jeeshop.catalog;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Category;
import org.rembx.jeeshop.catalog.model.Product;
import org.rembx.jeeshop.catalog.test.PresentationTexts;
import org.rembx.jeeshop.catalog.test.TestCatalog;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;
import static org.rembx.jeeshop.catalog.test.Assertions.assertThat;
import static org.rembx.jeeshop.catalog.test.Assertions.*;

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
        service = new CategoryResource(entityManager, new CatalogItemFinder(entityManager));
    }

    @Test
    public void find_withIdOfVisibleCategory_ShouldReturnExpectedCategory() {
        Category category = service.find(testCatalog.aCategoryWithProducts().getId(), null);
        assertThat(category).isEqualTo(testCatalog.aCategoryWithProducts());
        assertThat(category.isVisible()).isTrue();

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
            service.findChildCategories(9999L, null);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertEquals(Response.Status.NOT_FOUND,e.getResponse().getStatusInfo());
        }
    }

    @Test
    public void findCategories_shouldNotReturnExpiredNorDisabledCategories() {
        List<Category> categories = service.findChildCategories(testCatalog.aRootCategoryWithChildCategories().getId(), null);
        assertNotNull(categories);
        assertThatCategoriesOf(categories).areVisibleChildCategoriesOfARootCategoryWithChildCategories();
    }

    @Test
    public void findCategories_shouldReturnEmptyListWhenNoChildCategories() {
        List<Category> categories = service.findChildCategories(testCatalog.aCategoryWithProducts().getId(), null);
        assertThat(categories).isEmpty();
    }

    @Test
    public void findProducts_shouldReturn404ExWhenCategoryNotFound() {
        try{
            service.findChildProducts(9999L, null);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertEquals(Response.Status.NOT_FOUND,e.getResponse().getStatusInfo());
        }
    }

    @Test
    public void findProducts_shouldNotReturnExpiredNorDisabledProducts() {
        List<Product> products = service.findChildProducts(testCatalog.aCategoryWithProducts().getId(), null);
        assertNotNull(products);
        assertThatProductsOf(products).areVisibleProductsOfAChildCategoryWithProducts();
    }

    @Test
    public void findProducts_shouldReturnEmptyListWhenNoChildProducts() {
        List<Product> products = service.findChildProducts(testCatalog.aCategoryWithoutProducts().getId(), null);
        assertThat(products).isEmpty();
    }

    @Test
    public void findAll_shouldReturnNoneEmptyList() {
        assertThat(service.findAll(null, null)).isNotEmpty();
    }

    @Test
    public void findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        List<Category> categories = service.findAll(0, 1);
        assertThat(categories).isNotEmpty();
        assertThat(categories).hasSize(1);
    }

}