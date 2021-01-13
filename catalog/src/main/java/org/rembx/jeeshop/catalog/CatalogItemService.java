package org.rembx.jeeshop.catalog;

import org.rembx.jeeshop.catalog.model.CatalogItem;
import org.rembx.jeeshop.rest.WebApplicationException;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.Set;

import static org.rembx.jeeshop.role.JeeshopRoles.ADMIN;
import static org.rembx.jeeshop.role.JeeshopRoles.STORE_ADMIN;

public interface CatalogItemService<T extends CatalogItem> {

    List<T> findAll(String search, Integer start, Integer size, String orderBy, Boolean isDesc, String locale);

    T find(SecurityContext securityContext, @NotNull Long productId, String locale);

    T create(SecurityContext securityContext, T item);

    T modify(SecurityContext securityContext, T item);

    void delete(SecurityContext securityContext, Long itemId);

    Set<String> findPresentationsLocales(SecurityContext securityContext, @NotNull Long catalogId);

    PresentationResource findPresentationByLocale(@NotNull Long productId, @NotNull String locale);

    default void attachOwner(SecurityContext securityContext, CatalogItem catalogItem) {
        if (securityContext.isUserInRole(ADMIN) && catalogItem.getOwner() == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);

        } else if (securityContext.isUserInRole(STORE_ADMIN) && catalogItem.getOwner() == null) {
            catalogItem.setOwner(securityContext.getUserPrincipal().getName());
        }
    }
}
