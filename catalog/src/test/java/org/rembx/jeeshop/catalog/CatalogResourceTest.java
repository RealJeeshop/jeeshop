package org.rembx.jeeshop.catalog;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Category;
import org.rembx.jeeshop.catalog.util.TestCatalog;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.rembx.jeeshop.catalog.util.Assertions.assertThatCategoriesOf;

public class CatalogResourceTest {

    private CatalogResource service;

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
        service = new CatalogResource(entityManager,new CatalogItemFinder(entityManager));
    }

    @Test
    public void findCategories_shouldReturn404ExWhenCatalogNotFound() {
        try{
            service.findCategories(9999L);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test
    public void findCategories_shouldReturnEmptyListWhenCatalogIsEmpty() {
        List<Category> categories = service.findCategories(testCatalog.getEmptyCatalogId());
        assertThat(categories).isEmpty();
    }

    @Test
    public void findCategories_shouldNotReturnExpiredNorDisabledRootCategories() {
        List<Category> categories = service.findCategories(testCatalog.getId());
        assertThatCategoriesOf(categories).areVisibleRootCategories();
    }
}