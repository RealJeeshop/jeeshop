package org.rembx.jeeshop.catalog;

import org.junit.Before;
import org.junit.Test;
import org.rembx.jeeshop.catalog.model.Category;
import org.rembx.jeeshop.catalog.util.TestCatalog;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.Assert.*;
import static org.rembx.jeeshop.catalog.util.Assertions.assertThatCategoriesOf;

public class CatalogServiceTest {

    private CatalogService service;

    private TestCatalog testCatalog;

    @Before
    public void setup(){
        testCatalog = TestCatalog.getInstance();
        service = new CatalogService(testCatalog.getEntityManager());
    }

    @Test
    public void findCategories_shouldReturn404ExWhenCatalogNotFound() {
        try{
            service.findCategories(9999L);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertEquals(Response.Status.NOT_FOUND,e.getResponse().getStatusInfo());
        }
    }

    @Test
    public void findCategories_shouldNotReturnExpiredNorDisabledRootCategories() {
        List<Category> categories = service.findCategories(testCatalog.getId());
        assertNotNull(categories);
        assertThatCategoriesOf(categories).areVisibleRootCategories();
    }
}