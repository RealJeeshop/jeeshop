package org.rembx.jeeshop.catalog;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rembx.jeeshop.catalog.model.Catalog;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Category;
import org.rembx.jeeshop.catalog.test.Assertions;
import org.rembx.jeeshop.catalog.test.TestCatalog;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.rembx.jeeshop.catalog.test.Assertions.assertThatCategoriesOf;

public class CatalogsResourceIT {

    private Catalogs service;

    private TestCatalog testCatalog;
    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @BeforeClass
    public static void beforeClass(){
        entityManagerFactory = Persistence.createEntityManagerFactory(CatalogPersistenceUnit.NAME);
    }

    @Before
    public void setup(){
        testCatalog = TestCatalog.getInstance();
        entityManager = entityManagerFactory.createEntityManager();
        service = new Catalogs(entityManager,new CatalogItemFinder(entityManager));
    }

    @Test
    public void findCategories_shouldReturn404ExWhenCatalogNotFound() {
        try{
            service.findCategories(9999L, null);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test
    public void findCategories_shouldReturnEmptyListWhenCatalogIsEmpty() {
        List<Category> categories = service.findCategories(testCatalog.getEmptyCatalogId(),null);
        assertThat(categories).isEmpty();
    }

    @Test
    public void findCategories_shouldNotReturnExpiredNorDisabledRootCategories() {
        List<Category> categories = service.findCategories(testCatalog.getId(),null);
        assertThatCategoriesOf(categories).areVisibleRootCategories();
    }

    @Test
    public void findAll_shouldReturnNoneEmptyList() {
        assertThat(service.findAll(null,null, null)).isNotEmpty();
    }

    @Test
    public void findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        List<Catalog> catalogs = service.findAll(null,0, 1);
        assertThat(catalogs).isNotEmpty();
        assertThat(catalogs).hasSize(1);
    }

    @Test
    public void findAll_withIdSearchParam_shouldReturnResultsWithMatchingId() {
        assertThat(service.findAll(testCatalog.getId().toString(),null, null)).containsExactly(TestCatalog.getCatalog());
    }

    @Test
    public void findAll_withNameSearchParam_shouldReturnResultsWithMatchingName() {
        assertThat(service.findAll(TestCatalog.getCatalog().getName(),null, null)).containsExactly(TestCatalog.getCatalog());
    }

    @Test
    public void find_withIdOfVisibleCatalog_ShouldReturnExpectedCatalog() {
        Catalog catalog = service.find(testCatalog.getId(), null);
        Assertions.assertThat(catalog).isNotNull();
        Assertions.assertThat(catalog.isVisible()).isTrue();
    }

    @Test
    public void modifyCatalog_ShouldModifyCatalogAttributesAndPreserveRootCategoriesWhenNotProvided() {
        Catalog catalog = service.find(testCatalog.getId(), null);

        Catalog detachedCatalogToModify = new Catalog(1L,catalog.getName());
        
        service.modify(detachedCatalogToModify);

        assertThat(catalog.getRootCategories()).isNotEmpty();

    }

    @Test
    public void modifyUnknownCatalog_ShouldThrowNotFoundException() {

        Catalog detachedCatalogToModify = new Catalog(9999L,null);
        try {
            service.modify(detachedCatalogToModify);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertThat(e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode());
        }
    }

    @Test
    public void countAll(){
        assertThat(service.count(null)).isGreaterThan(0);
    }

    @Test
    public void countAll_withUnknownSearchCriteria(){
        assertThat(service.count("666")).isEqualTo(0);
    }

    @Test
    public void create_shouldPersist(){
        Catalog catalog = new Catalog("New Test Catalog");

        entityManager.getTransaction().begin();
        service.create(catalog);
        entityManager.getTransaction().commit();

        assertThat(entityManager.find(Catalog.class, catalog.getId())).isNotNull();
        entityManager.remove(catalog);
    }

    @Test
    public void delete_shouldRemove(){

        entityManager.getTransaction().begin();
        Catalog catalog = new Catalog("Test Catalog");
        entityManager.persist(catalog);
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        service.delete(catalog.getId());
        entityManager.getTransaction().commit();

        assertThat(entityManager.find(Catalog.class, catalog.getId())).isNull();
    }

    @Test
    public void delete_NotExistingEntry_shouldThrowNotFoundEx(){

        try {
            entityManager.getTransaction().begin();
            service.delete(666L);
            entityManager.getTransaction().commit();
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertThat(e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode());
        }
    }

}