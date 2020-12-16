package org.rembx.jeeshop.catalog;

import org.rembx.jeeshop.catalog.model.CatalogItem;

import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.Set;

public interface CatalogItems<T extends CatalogItem> {

    List<T> findAll(String search, Integer start, Integer size, String orderBy, Boolean isDesc, String locale);

    T find(SecurityContext securityContext, Long itemId, String locale);

    T create(SecurityContext securityContext, T item);

    T modify(SecurityContext securityContext, T item);

    void delete(SecurityContext securityContext, Long itemId);

    Set<String> findPresentationsLocales(SecurityContext securityContext, Long itemId);

    PresentationResource findPresentationByLocale(Long itemId, String locale);
}
