package org.rembx.jeeshop.catalog;

import org.apache.http.auth.BasicUserPrincipal;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rembx.jeeshop.catalog.model.Catalog;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Category;
import org.rembx.jeeshop.catalog.test.TestCatalog;
import org.rembx.jeeshop.rest.WebApplicationException;
import org.rembx.jeeshop.role.JeeshopRoles;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.rembx.jeeshop.catalog.test.Assertions.assertThatCategoriesOf;

public class CatalogsCT {

    private Catalogs service;

    private TestCatalog testCatalog;
    private static EntityManagerFactory entityManagerFactory;
    EntityManager entityManager;
    private SecurityContext securityContext;

    @BeforeAll
    public static void beforeClass() {
        entityManagerFactory = Persistence.createEntityManagerFactory(CatalogPersistenceUnit.NAME);
    }

    @BeforeEach
    public void setup() {
        testCatalog = TestCatalog.getInstance();
        entityManager = entityManagerFactory.createEntityManager();
        securityContext = mock(SecurityContext.class);
        service = new Catalogs(entityManager, new CatalogItemFinder(entityManager), null);
    }

    @Test
    public void findCategories_shouldReturn404ExWhenCatalogNotFound() {
        try {
            service.findCategories(null, 9999L, null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test
    public void findCategories_shouldReturnEmptyListWhenCatalogIsEmpty() {
        List<Category> categories = service.findCategories(null, testCatalog.getEmptyCatalogId(), null);
        assertThat(categories).isEmpty();
    }

    @Test
    public void findCategories_shouldNotReturnExpiredNorDisabledRootCategories() {

        when(securityContext.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(false);
        when(securityContext.isUserInRole(JeeshopRoles.STORE_ADMIN)).thenReturn(false);
        List<Category> categories = service.findCategories(securityContext, testCatalog.getId(), null);
        assertThatCategoriesOf(categories).areVisibleRootCategories();
    }

    @Test
    public void findAll_shouldReturnNoneEmptyList() {
        assertThat(service.findAll(null, null, null, null, null, null)).isNotEmpty();
    }

    @Test
    public void findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        List<Catalog> catalogs = service.findAll(null, 0, 1, null, null, null);
        assertThat(catalogs).isNotEmpty();
        assertThat(catalogs).hasSize(1);
    }

    @Test
    public void findAll_withIdSearchParam_shouldReturnResultsWithMatchingId() {
        assertThat(service.findAll(testCatalog.getId().toString(), null, null, null, null, null)).containsExactly(TestCatalog.getCatalog());
    }

    @Test
    public void findAll_withNameSearchParam_shouldReturnResultsWithMatchingName() {
        assertThat(service.findAll(TestCatalog.getCatalog().getName(), null, null, null, null, null)).containsExactly(TestCatalog.getCatalog());
    }

    @Test
    public void find_withIdOfVisibleCatalog_ShouldReturnExpectedCatalog() {

        setUpSecurityMock(TestCatalog.OWNER);

        Catalog catalog = service.find(securityContext, testCatalog.getId(), null);
        assertThat(catalog).isNotNull();
        assertThat(catalog.isVisible()).isTrue();
    }

    @Test
    public void modifyCatalog_ShouldModifyCatalogAttributesAndPreserveRootCategoriesWhenNotProvided() {

        setUpSecurityMock(TestCatalog.OWNER);

        Catalog catalog = service.find(securityContext, testCatalog.getId(), null);

        Catalog detachedCatalogToModify = new Catalog(1L, catalog.getName());

        setUpSecurityMock(TestCatalog.OWNER);

        service.modify(securityContext, detachedCatalogToModify);

        assertThat(catalog.getRootCategories()).isNotEmpty();

    }

    private void setUpSecurityMock(String owner) {
        when(securityContext.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(false);
        when(securityContext.isUserInRole(JeeshopRoles.STORE_ADMIN)).thenReturn(true);
        when(securityContext.getUserPrincipal()).thenReturn(new BasicUserPrincipal(owner));
    }

    @Test
    public void modifyUnknownCatalog_ShouldThrowNotFoundException() {

        Catalog detachedCatalogToModify = new Catalog(9999L, null);
        try {
            setUpSecurityMock(TestCatalog.OWNER);

            service.modify(securityContext, detachedCatalogToModify);
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
        Catalog catalog = new Catalog("New Test Catalog");

        setUpSecurityMock("777@test.com");

        entityManager.getTransaction().begin();
        service.create(securityContext, catalog);
        entityManager.getTransaction().commit();

        assertThat(entityManager.find(Catalog.class, catalog.getId())).isNotNull();
        entityManager.remove(catalog);
    }

    @Test
    public void delete_shouldRemove() {

        entityManager.getTransaction().begin();
        Catalog catalog = new Catalog("Test Catalog");
        catalog.setOwner(TestCatalog.OWNER);
        entityManager.persist(catalog);
        entityManager.getTransaction().commit();

        setUpSecurityMock(TestCatalog.OWNER);

        entityManager.getTransaction().begin();
        service.delete(securityContext, catalog.getId());
        entityManager.getTransaction().commit();

        assertThat(entityManager.find(Catalog.class, catalog.getId())).isNull();
    }

    @Test
    public void delete_NotExistingEntry_shouldThrowNotFoundEx() {

        try {
            entityManager.getTransaction().begin();
            setUpSecurityMock(TestCatalog.OWNER);
            service.delete(securityContext, 666L);
            entityManager.getTransaction().commit();
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

}