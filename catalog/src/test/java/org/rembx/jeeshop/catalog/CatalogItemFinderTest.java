package org.rembx.jeeshop.catalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rembx.jeeshop.catalog.model.Catalog;
import org.rembx.jeeshop.catalog.model.CatalogItem;
import org.rembx.jeeshop.rest.WebApplicationException;

import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CatalogItemFinderTest {

    private CatalogItemFinder instance;

    CatalogItem visibleCatalogItem = new CatalogItem() {
        @Override
        public boolean isVisible() {
            return true;
        }
    };


    @BeforeEach
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