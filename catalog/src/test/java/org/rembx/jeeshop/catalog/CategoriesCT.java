package org.rembx.jeeshop.catalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rembx.jeeshop.catalog.model.Category;
import org.rembx.jeeshop.catalog.model.Product;
import org.rembx.jeeshop.catalog.test.CatalogItemCRUDTester;
import org.rembx.jeeshop.catalog.test.PresentationTexts;
import org.rembx.jeeshop.catalog.test.TestCatalog;
import org.rembx.jeeshop.rest.WebApplicationException;

import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.rembx.jeeshop.catalog.test.Assertions.*;

public class CategoriesCT {

    private Categories localService;
    private CatalogItemCRUDTester<Category> tester;

    @BeforeEach
    public void setup() {
        tester = new CatalogItemCRUDTester<>(Category.class);
        localService = new Categories(tester.getEntityManager(), new CatalogItemFinder(tester.getEntityManager()), null);
        tester.setService(this.localService);
    }

    @Test
    public void find_withIdOfVisibleCategory_ShouldReturnExpectedCategory() {
        tester.setAdminUser();
        Category category = localService.find(tester.getSecurityContext(), tester.getFixtures().aCategoryWithProducts().getId(), null);
        assertThat(category).isEqualTo(tester.getFixtures().aCategoryWithProducts());
        assertThat(category.isVisible()).isTrue();

    }

    @Test
    public void find_withIdOfDisableCategory_ShouldThrowForbiddenException() {
        try {
            tester.setPublicUser();
            localService.find(tester.getSecurityContext(), tester.getFixtures().aDisabledCategory().getId(), null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.FORBIDDEN);
        }
    }

    @Test
    public void find_withIdOfExpiredCategory_ShouldThrowForbiddenException() {
        try {
            tester.setPublicUser();
            localService.find(tester.getSecurityContext(), tester.getFixtures().anExpiredCategory().getId(), null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.FORBIDDEN);
        }
    }

    @Test
    public void find_idOfCategoryWithPresentation_ShouldReturnExpectedPresentation() {
        tester.setPublicUser();
        Category actual = localService.find(tester.getSecurityContext(), tester.getFixtures().aCategoryWithPresentation().getId(), Locale.ENGLISH.toString());
        assertThat(actual).hasLocalizedPresentationShortDescription(Locale.ENGLISH.toString(), PresentationTexts.TEXT_1000);
    }

    @Test
    public void find_idOfCategoryWithPresentation_WithNoLocaleSpecifiedShouldReturnFallbackLocalePresentation() {
        tester.setPublicUser();
        Category category = localService.find(tester.getSecurityContext(),
                tester.getFixtures().aCategoryWithPresentation().getId(), null);
        assertThat(category).hasLocalizedPresentationShortDescription(Locale.ENGLISH.toString(), PresentationTexts.TEXT_1000);
    }

    @Test
    public void find_idOfCategoryWithPresentation_WithNotSupportedLocaleSpecifiedShouldReturnFallbackLocalePresentation() {
        tester.setPublicUser();
        Category category = localService.find(tester.getSecurityContext(), tester.getFixtures().aCategoryWithPresentation().getId(), "it_IT");
        assertThat(category).hasLocalizedPresentationShortDescription(Locale.ENGLISH.toString(), PresentationTexts.TEXT_1000);
    }

    @Test
    public void findLocales_OfACategoryWithPresentations_shouldReturnExpectedPresentations() {
        assertThat(localService.findPresentationsLocales(tester.getFixtures().aCategoryWithPresentation().getId())).containsOnly(Locale.US.toString(), Locale.ENGLISH.toString());
    }

    @Test
    public void find_withUnknownCategoryId_ShouldThrowNotFoundException() {
        try {
            tester.setAdminUser();
            localService.find(tester.getSecurityContext(), 9999L, null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test
    public void findCategories_shouldReturn404ExWhenCategoryNotFound() {
        try {
            tester.setAdminUser();
            localService.findChildCategories(tester.getSecurityContext(), 9999L, null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test
    public void findCategories_shouldNotReturnExpiredNorDisabledCategories() {
        tester.setPublicUser();
        List<Category> categories = localService.findChildCategories(tester.getSecurityContext(),
                tester.getFixtures().aRootCategoryWithChildCategories().getId(), null);
        assertThat(categories).isNotEmpty();
        assertThatCategoriesOf(categories).areVisibleChildCategoriesOfARootCategoryWithChildCategories();
    }

    @Test
    public void findCategories_shouldReturnEmptyListWhenNoChildCategories() {
        tester.setAdminUser();
        List<Category> categories = localService.findChildCategories(tester.getSecurityContext(),
                tester.getFixtures().aCategoryWithProducts().getId(), null);
        assertThat(categories).isEmpty();
    }

    @Test
    public void findProducts_shouldReturn404ExWhenCategoryNotFound() {
        try {
            tester.setAdminUser();
            localService.findChildProducts(tester.getSecurityContext(), 9999L, null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test
    public void findProducts_shouldNotReturnExpiredNorDisabledProducts() {
        tester.setAdminUser();
        List<Product> products = localService.findChildProducts(tester.getSecurityContext(),
                tester.getFixtures().aCategoryWithProducts().getId(), null);
        assertThat(products).isNotEmpty();
        assertThatProductsOf(products).areVisibleProductsOfAChildCategoryWithProducts();
    }

    @Test
    public void findProducts_shouldReturnEmptyListWhenNoChildProducts() {
        tester.setAdminUser();
        List<Product> products = localService.findChildProducts(tester.getSecurityContext(),
                tester.getFixtures().aCategoryWithoutProducts().getId(), null);
        assertThat(products).isEmpty();
    }

    @Test
    public void findAll_shouldReturnNoneEmptyList() {
        assertThat(localService.findAll(null, null, null, null, null, null)).isNotEmpty();
    }

    @Test
    public void findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        List<Category> categories = localService.findAll(null, 0, 1, null, null, null);
        assertThat(categories).isNotEmpty();
        assertThat(categories).hasSize(1);
    }

    @Test
    public void findAll_withIdSearchParam_shouldReturnResultsWithMatchingId() {
        assertThat(localService.findAll(tester.getFixtures().aCategoryWithoutProducts().getId().toString(), null, null, null, null, null)).containsExactly(tester.getFixtures().aCategoryWithoutProducts());
    }

    @Test
    public void findAll_withNameSearchParam_shouldReturnResultsWithMatchingName() {
        assertThat(localService.findAll(tester.getFixtures().aCategoryWithoutProducts().getName(), null, null, null, null, null)).containsExactly(tester.getFixtures().aCategoryWithoutProducts());
    }

    @Test
    public void modifyCategory_ShouldModifyCategoryAttributesAndPreserveCategoriesWhenNotProvided() {

        tester.setAdminUser();
        Category category = new Category(tester.getFixtures().aRootCategoryWithChildCategories().getId(), "New name");
        tester.test_modify(category);
        assertThat(category.getName()).isEqualTo("New name");
        assertThat(category.getChildCategories()).isNotEmpty();
    }

    @Test
    public void modifyCategory_ShouldModifyCategoryAttributesAndPreserveChildProductsWhenNotProvided() {

        tester.setAdminUser();
        Category category = new Category(tester.getFixtures().aCategoryWithProducts().getId(), "New name");
        ;
        tester.test_modify(category);
        assertThat(category.getName()).isEqualTo("New name");
        assertThat(category.getChildProducts()).isNotEmpty();
    }

    @Test
    public void modifyUnknownCategory_ShouldThrowNotFoundException() {

        try {
            Category category = new Category(9999L, null);
            localService.modify(tester.getSecurityContext(), category);
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
    public void create_shouldSetupOwner_for_admin() {

        tester.setAdminUser();
        Category category = new Category("name", "description", new Date(), new Date(), false, "test@test.com");
        category.setOwner(TestCatalog.OWNER);

        Category actualCategory = tester.test_create(category);

        assertThat(actualCategory).isNotNull();
        assertThat(actualCategory.getOwner()).isEqualTo(TestCatalog.OWNER);
    }

    @Test
    public void create_shouldSetupOwner_for_store_admin() {

        tester.setStoreAdminUser();
        Category category = new Category("name", "");
        Category actualCategory = tester.test_create(category);

        assertThat(actualCategory).isNotNull();
        assertThat(actualCategory.getOwner()).isEqualTo(TestCatalog.OWNER);
    }

    @Test
    public void create_withoutOwner_shouldThrow_BadRequestEx_for_admin() {

        try {
            tester.setAdminUser();
            Category category = new Category("name", "description");
            tester.test_create(category);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.BAD_REQUEST);
        }
    }

    @Test
    public void delete_shouldRemove() {

        tester.setStoreAdminUser();
        Category category = new Category("Test category", "");
        category.setOwner(TestCatalog.OWNER);
        tester.test_delete(category);
        assertThat(tester.getEntityManager().find(Category.class, category.getId())).isNull();
    }

    @Test
    public void delete_NotExistingEntry_shouldThrowNotFoundEx() {

        try {
            tester.setStoreAdminUser();
            localService.delete(tester.getSecurityContext(), 666L);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test
    public void delete_NonManagedEntry_shouldThrowForbiddenEx() {

        try {
            tester.setStoreAdminUser();
            Category category = new Category("Test category", "");
            category.setOwner("test@test.com");
            tester.test_delete(category);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.FORBIDDEN);
        }
    }
}