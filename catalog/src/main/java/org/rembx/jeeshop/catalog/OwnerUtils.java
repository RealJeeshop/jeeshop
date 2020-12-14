package org.rembx.jeeshop.catalog;

import org.rembx.jeeshop.catalog.model.CatalogItem;
import org.rembx.jeeshop.rest.WebApplicationException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import static org.rembx.jeeshop.role.JeeshopRoles.ADMIN;
import static org.rembx.jeeshop.role.JeeshopRoles.STORE_ADMIN;

public class OwnerUtils {

    public static void attachOwner(SecurityContext securityContext, CatalogItem catalogItem) {
        if (securityContext.isUserInRole(ADMIN) && catalogItem.getOwner() == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);

        } else if (securityContext.isUserInRole(STORE_ADMIN) && catalogItem.getOwner() == null) {
            catalogItem.setOwner(securityContext.getUserPrincipal().getName());
        }
    }
}
