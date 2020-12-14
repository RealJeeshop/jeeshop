package org.rembx.jeeshop.catalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rembx.jeeshop.catalog.model.Catalog;
import org.rembx.jeeshop.catalog.model.Category;
import org.rembx.jeeshop.catalog.test.CatalogItemCRUDTester;
import org.rembx.jeeshop.catalog.test.TestCatalog;
import org.rembx.jeeshop.rest.WebApplicationException;
import org.rembx.jeeshop.role.JeeshopRoles;

import javax.ws.rs.core.Response;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.rembx.jeeshop.catalog.test.Assertions.assertThatCategoriesOf;

public class CatalogsCT extends CatalogItemCRUDTester<Catalog> {

    private Catalogs localService;

    @Override
    protected Class<Catalog> getItemClass() {
        return Catalog.class;
    }

    @Override
    protected CatalogItems<Catalog> getService() {
        return this.localService;
    }

    @BeforeEach
    public void setup() {
        this.localService = new Catalogs(entityManager, new CatalogItemFinder(entityManager), null);;
    }

    @Test
    public void findCategories_shouldReturn404ExWhenCatalogNotFound() {
        try {
            localService.findCategories(null, 9999L, null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test
    public void findCategories_shouldReturnEmptyListWhenCatalogIsEmpty() {
        List<Category> categories = localService.findCategories(null, testCatalog.getEmptyCatalogId(), null);
        assertThat(categories).isEmpty();
    }

    @Test
    public void findCategories_shouldNotReturnExpiredNorDisabledRootCategories() {

        when(securityContext.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(false);
        when(securityContext.isUserInRole(JeeshopRoles.STORE_ADMIN)).thenReturn(false);
        List<Category> categories = localService.findCategories(securityContext, testCatalog.getId(), null);
        assertThatCategoriesOf(categories).areVisibleRootCategories();
    }

    @Test
    public void findAll_shouldReturnNoneEmptyList() {
        assertThat(localService.findAll(null, null, null, null, null, null)).isNotEmpty();
    }

    @Test
    public void findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        List<Catalog> catalogs = localService.findAll(null, 0, 1, null, null, null);
        assertThat(catalogs).isNotEmpty();
        assertThat(catalogs).hasSize(1);
    }

    @Test
    public void findAll_withIdSearchParam_shouldReturnResultsWithMatchingId() {
        assertThat(localService.findAll(testCatalog.getId().toString(), null, null, null, null, null)).containsExactly(TestCatalog.getCatalog());
    }

    @Test
    public void findAll_withNameSearchParam_shouldReturnResultsWithMatchingName() {
        assertThat(localService.findAll(TestCatalog.getCatalog().getName(), null, null, null, null, null)).containsExactly(TestCatalog.getCatalog());
    }

    @Test
    public void find_withIdOfVisibleCatalog_ShouldReturnExpectedCatalog() {

        setStoreAdminUser();

        Catalog catalog = localService.find(securityContext, testCatalog.getId(), null);
        assertThat(catalog).isNotNull();
        assertThat(catalog.isVisible()).isTrue();
    }

    @Test
    public void modifyCatalog_ShouldModifyCatalogAttributesAndPreserveRootCategoriesWhenNotProvided() {

        setStoreAdminUser();

        Catalog detachedCatalogToModify = new Catalog(2L, "New name");

        test_modify(detachedCatalogToModify);

        assertThat(detachedCatalogToModify.getName()).isEqualTo("New name");
        assertThat(detachedCatalogToModify.getRootCategories()).isNotEmpty();

    }

    @Test
    public void modifyUnknownCatalog_ShouldThrowNotFoundException() {

        setStoreAdminUser();
        Catalog detachedCatalogToModify = new Catalog(9999L, null);
        try {
            test_modify(detachedCatalogToModify);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test
    public void modifyNonManagedCatalog_ShouldThrowForbiddenException() {

        setSAnotherStoreAdminUser();
        Catalog detachedCatalogToModify = new Catalog(1L, "name");

        try {
            test_modify(detachedCatalogToModify);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.FORBIDDEN);
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

        setAdminUser();
        Catalog catalog = new Catalog("Catalog");
        catalog.setOwner(TestCatalog.OWNER);

        Catalog actualCatalog = test_create(catalog);

        assertThat(actualCatalog).isNotNull();
        assertThat(actualCatalog.getOwner()).isEqualTo(TestCatalog.OWNER);
    }

    @Test
    public void create_shouldThrowBadRequest_whenOwnerIsNull_for_admin() {

        setAdminUser();
        Catalog catalog = new Catalog("Catalog");

        try {
            test_create(catalog);
            fail("should have thrown an exception");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.BAD_REQUEST);
        }
    }

    @Test
    public void create_shouldSetupOwner_for_store_admin() {

        setStoreAdminUser();
        Catalog catalog = new Catalog("Catalog");

        Catalog actualCatalog = test_create(catalog);

        assertThat(actualCatalog).isNotNull();
        assertThat(actualCatalog.getOwner()).isEqualTo(TestCatalog.OWNER);
    }

    @Test
    public void delete_shouldRemove() {

        setStoreAdminUser();

        Catalog catalog = new Catalog("Test Catalog");
        catalog.setOwner(TestCatalog.OWNER);

        test_delete(catalog);

        assertThat(entityManager.find(Catalog.class, catalog.getId())).isNull();
    }

    @Test
    public void delete_shouldThrowForbidden_for_store_admin() {

        try {
            setStoreAdminUser();
            Catalog catalog = new Catalog("catalog");
            catalog.setOwner("test@test.org");
            test_delete(catalog);
            fail("Should have throw an exception");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.FORBIDDEN);
        }
    }

    @Test
    public void delete_NotExistingEntry_shouldThrowNotFoundEx() {

        try {
            setStoreAdminUser();
            localService.delete(securityContext, 666L);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

}