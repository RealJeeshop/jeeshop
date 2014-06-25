package org.rembx.jeeshop.catalog;

import org.junit.Before;
import org.junit.Test;
import org.rembx.jeeshop.catalog.model.Catalog;
import org.rembx.jeeshop.catalog.model.CatalogItem;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CatalogItemFinderTest {

    private CatalogItemFinder instance;

    CatalogItem visibleCatalogItem = new CatalogItem() {
        @Override
        public boolean isVisible() {
            return true;
        }
    };


    @Before
    public void setup() {
        instance = new CatalogItemFinder();
    }


    @Test
    public void find_VisibleCatalogItem_ShouldReturnExpectedProduct() {
        assertThat(instance.filterVisible(visibleCatalogItem, null)).isEqualTo(visibleCatalogItem);
    }

    @Test
    public void find_NotVisibleCatalogItem_ShouldThrowForbiddenException() {
        try {
            instance.filterVisible(new Catalog(), null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertEquals(Response.Status.FORBIDDEN, e.getResponse().getStatusInfo());
        }
    }

    @Test
    public void find_NullCatalogItem_ShouldThrowNotFoundException() {
        try {
            instance.filterVisible(null, null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertEquals(Response.Status.NOT_FOUND, e.getResponse().getStatusInfo());
        }
    }

}