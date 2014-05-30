package org.rembx.jeeshop.catalog.util;

import org.rembx.jeeshop.catalog.model.CatalogItem;
import org.rembx.jeeshop.catalog.model.SKU;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Created by remi on 30/05/14.
 */
public class CatalogItemResourceUtil {

    public <T extends CatalogItem> T find(T catalogItem, String locale){

        if (catalogItem == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        if (!catalogItem.isVisible()){
            throw new WebApplicationException((Response.Status.FORBIDDEN));
        }

        catalogItem.setLocalizedPresentation(locale);

        return catalogItem;
    }
}
