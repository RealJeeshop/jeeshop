package org.rembx.jeeshop.catalog.util;

import org.junit.Before;
import org.junit.Test;
import org.rembx.jeeshop.catalog.model.Catalog;
import org.rembx.jeeshop.catalog.model.CatalogItem;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CatalogItemResourceUtilTest {

    CatalogItem visibleCatalogItem = new CatalogItem() {
        @Override
        public boolean isVisible() {
            return true;
        }
    };

    CatalogItemResourceUtil instance;

    @Before
    public void setup() {
        instance = new CatalogItemResourceUtil();
    }

    @Test
    public void find_VisibleCatalogItem_ShouldReturnExpectedProduct() {
        assertThat(instance.find(visibleCatalogItem, null)).isEqualTo(visibleCatalogItem);
    }

    @Test
    public void find_NotVisibleCatalogItem_ShouldThrowForbiddenException() {
        try {
            instance.find(new Catalog(), null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertEquals(Response.Status.FORBIDDEN, e.getResponse().getStatusInfo());
        }
    }

    @Test
    public void find_NullCatalogItem_ShouldThrowNotFoundException() {
        try {
            instance.find(null, null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertEquals(Response.Status.NOT_FOUND, e.getResponse().getStatusInfo());
        }
    }


}