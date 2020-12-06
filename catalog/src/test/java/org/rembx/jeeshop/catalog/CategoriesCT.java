package org.rembx.jeeshop.catalog;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Category;
import org.rembx.jeeshop.catalog.model.Product;
import org.rembx.jeeshop.catalog.test.PresentationTexts;
import org.rembx.jeeshop.catalog.test.TestCatalog;
import org.rembx.jeeshop.rest.WebApplicationException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.rembx.jeeshop.catalog.test.Assertions.*;


public class CategoriesCT {
    private Categories service;

    private TestCatalog testCatalog;
    private static EntityManagerFactory entityManagerFactory;
    EntityManager entityManager;

    @BeforeAll
    public static void beforeClass() {
        entityManagerFactory = Persistence.createEntityManagerFactory(CatalogPersistenceUnit.NAME);
    }

    @BeforeEach
    public void setup() {
        testCatalog = TestCatalog.getInstance();
        entityManager = entityManagerFactory.createEntityManager();
        service = new Categories(entityManager, new CatalogItemFinder(entityManager), null);
    }

    @Test
    public void find_withIdOfVisibleCategory_ShouldReturnExpectedCategory() {
        Category category = service.find(testCatalog.aCategoryWithProducts().getId(), null);
        assertThat(category).isEqualTo(testCatalog.aCategoryWithProducts());
        assertThat(category.isVisible()).isTrue();

    }

    @Test
    public void find_withIdOfDisableCategory_ShouldThrowForbiddenException() {
        try {
            service.find(testCatalog.aDisabledCategory().getId(), null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.FORBIDDEN);
        }
    }

    @Test
    public void find_withIdOfExpiredCategory_ShouldThrowForbiddenException() {
        try {
            service.find(testCatalog.anExpiredCategory().getId(), null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.FORBIDDEN);
        }
    }

    @Test
    public void find_idOfCategoryWithPresentation_ShouldReturnExpectedPresentation() {
        assertThat(service.find(testCatalog.aCategoryWithPresentation().getId(), Locale.ENGLISH.toString())).hasLocalizedPresentationShortDescription(Locale.ENGLISH.toString(), PresentationTexts.TEXT_1000);
    }

    @Test
    public void find_idOfCategoryWithPresentation_WithNoLocaleSpecifiedShouldReturnFallbackLocalePresentation() {
        assertThat(service.find(testCatalog.aCategoryWithPresentation().getId(), null)).hasLocalizedPresentationShortDescription(Locale.ENGLISH.toString(), PresentationTexts.TEXT_1000);
    }

    @Test
    public void find_idOfCategoryWithPresentation_WithNotSupportedLocaleSpecifiedShouldReturnFallbackLocalePresentation() {
        assertThat(service.find(testCatalog.aCategoryWithPresentation().getId(), "it_IT")).hasLocalizedPresentationShortDescription(Locale.ENGLISH.toString(), PresentationTexts.TEXT_1000);
    }

    @Test
    public void findLocales_OfACategoryWithPresentations_shouldReturnExpectedPresentations() {
        assertThat(service.findPresentationsLocales(testCatalog.aCategoryWithPresentation().getId())).containsOnly(Locale.US.toString(), Locale.ENGLISH.toString());
    }

    @Test
    public void find_withUnknownCategoryId_ShouldThrowNotFoundException() {
        try {
            service.find(9999L, null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test
    public void findCategories_shouldReturn404ExWhenCategoryNotFound() {
        try {
            service.findChildCategories(9999L, null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test
    public void findCategories_shouldNotReturnExpiredNorDisabledCategories() {
        List<Category> categories = service.findChildCategories(testCatalog.aRootCategoryWithChildCategories().getId(), null);
        assertThat(categories).isNotEmpty();
        assertThatCategoriesOf(categories).areVisibleChildCategoriesOfARootCategoryWithChildCategories();
    }

    @Test
    public void findCategories_shouldReturnEmptyListWhenNoChildCategories() {
        List<Category> categories = service.findChildCategories(testCatalog.aCategoryWithProducts().getId(), null);
        assertThat(categories).isEmpty();
    }

    @Test
    public void findProducts_shouldReturn404ExWhenCategoryNotFound() {
        try {
            service.findChildProducts(9999L, null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test
    public void findProducts_shouldNotReturnExpiredNorDisabledProducts() {
        List<Product> products = service.findChildProducts(testCatalog.aCategoryWithProducts().getId(), null);
        assertThat(products).isNotEmpty();
        assertThatProductsOf(products).areVisibleProductsOfAChildCategoryWithProducts();
    }

    @Test
    public void findProducts_shouldReturnEmptyListWhenNoChildProducts() {
        List<Product> products = service.findChildProducts(testCatalog.aCategoryWithoutProducts().getId(), null);
        assertThat(products).isEmpty();
    }

    @Test
    public void findAll_shouldReturnNoneEmptyList() {
        assertThat(service.findAll(null, null, null, null, null, null)).isNotEmpty();
    }

    @Test
    public void findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        List<Category> categories = service.findAll(null, 0, 1, null, null, null);
        assertThat(categories).isNotEmpty();
        assertThat(categories).hasSize(1);
    }

    @Test
    public void findAll_withIdSearchParam_shouldReturnResultsWithMatchingId() {
        assertThat(service.findAll(testCatalog.aCategoryWithoutProducts().getId().toString(), null, null, null, null, null)).containsExactly(testCatalog.aCategoryWithoutProducts());
    }

    @Test
    public void findAll_withNameSearchParam_shouldReturnResultsWithMatchingName() {
        assertThat(service.findAll(testCatalog.aCategoryWithoutProducts().getName(), null, null, null, null, null)).containsExactly(testCatalog.aCategoryWithoutProducts());
    }

    @Test
    public void modifyCategory_ShouldModifyCategoryAttributesAndPreserveCategoriesWhenNotProvided() {
        Category category = service.find(testCatalog.aRootCategoryWithChildCategories().getId(), null);

        Category detachedCategoryToModify = new Category(testCatalog.aRootCategoryWithChildCategories().getId(), category.getName(), category.getDescription(), category.getStartDate(), category.getEndDate(), category.isDisabled());

        service.modify(detachedCategoryToModify);

        assertThat(category.getChildCategories()).containsExactlyElementsOf(category.getChildCategories());

    }

    @Test
    public void modifyCategory_ShouldModifyCategoryAttributesAndPreserveChildProductsWhenNotProvided() {
        Category category = service.find(testCatalog.aCategoryWithProducts().getId(), null);

        Category detachedCategoryToModify = new Category(testCatalog.aCategoryWithProducts().getId(), category.getName(), category.getDescription(), category.getStartDate(), category.getEndDate(), category.isDisabled());
        detachedCategoryToModify.setDescription(category.getDescription());

        service.modify(detachedCategoryToModify);

        assertThat(category.getChildCategories()).containsExactlyElementsOf(category.getChildCategories());

    }

    @Test
    public void modifyUnknownCategory_ShouldThrowNotFoundException() {

        Category detachedCategoryToModify = new Category(9999L, null, null, null, null, null);
        try {
            service.modify(detachedCategoryToModify);
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
        Category category = new Category("name", "description", new Date(), new Date(), false);

        entityManager.getTransaction().begin();
        service.create(category);
        entityManager.getTransaction().commit();

        assertThat(entityManager.find(Category.class, category.getId())).isNotNull();
        entityManager.remove(category);
    }

    @Test
    public void delete_shouldRemove() {

        entityManager.getTransaction().begin();
        Category category = new Category("Test category", "");
        entityManager.persist(category);
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        service.delete(category.getId());
        entityManager.getTransaction().commit();

        assertThat(entityManager.find(Category.class, category.getId())).isNull();
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