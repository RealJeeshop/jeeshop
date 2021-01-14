package org.rembx.jeeshop.catalog;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.quarkus.hibernate.orm.PersistenceUnit;
import org.apache.commons.lang.math.NumberUtils;
import org.rembx.jeeshop.catalog.model.*;
import org.rembx.jeeshop.rest.WebApplicationException;
import org.rembx.jeeshop.role.JeeshopRoles;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for common finders on CatalogItem entities
 */
@ApplicationScoped
public class CatalogItemFinder {

    private EntityManager entityManager;
    EntityManagerFactory m;

    CatalogItemFinder(@PersistenceUnit(value = CatalogPersistenceUnit.NAME) EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public <T extends CatalogItem> List<T> findVisibleCatalogItems(EntityPath<T> entityPath, List<T> items, String locale) {
        QCatalogItem qCatalogItem = new QCatalogItem(entityPath);
        Date now = new Date();
        List<T> results = new JPAQueryFactory(entityManager)
                .selectFrom(entityPath).where(
                        qCatalogItem.disabled.isFalse(),
                        qCatalogItem.endDate.after(now).or(qCatalogItem.endDate.isNull()),
                        qCatalogItem.startDate.before(now).or(qCatalogItem.startDate.isNull()),
                        qCatalogItem.in(items)
                )
                .fetch();

        results.forEach((catalogItem) -> {
            catalogItem.setLocalizedPresentation(locale);
            mapCatalogItemChildrenPresentation(catalogItem, locale);
        });

        return results;

    }

    public <T extends CatalogItem> List<T> findAll(EntityPath<T> entityPath, Integer offset, Integer limit, String orderBy, Boolean isDesc, String locale) {
        QCatalogItem qCatalogItem = new QCatalogItem(entityPath);

        JPAQuery<T> query = new JPAQueryFactory(entityManager).selectFrom(entityPath);

        addOffsetAndLimitToQuery(offset, limit, query, orderBy, isDesc, qCatalogItem);

        List<T> catalogItems = query.fetch();
        return locale != null
                ? catalogItems.stream().peek(c -> {
                    c.setLocalizedPresentation(locale);
                    mapCatalogItemChildrenPresentation(c, locale);
                }).collect(Collectors.toList()) : catalogItems;
    }


    public <T extends CatalogItem, P extends CatalogItem> List<P> findForeignHolder(EntityPath<P> hp,
                                                                                    ListPath<T, ? extends SimpleExpression<T>> h, T c) {

        return new JPAQueryFactory(entityManager)
                .selectFrom(hp)
                .where(h.contains(c))
                .fetch();
    }

    public <T extends CatalogItem> List<T> findBySearchCriteria(EntityPath<T> entityPath, String searchCriteria,
                                                                Integer offset, Integer limit, String orderBy, Boolean isDesc, String locale) {
        QCatalogItem qCatalogItem = new QCatalogItem(entityPath);

        JPAQuery<T> query = new JPAQueryFactory(entityManager).selectFrom(entityPath)
                .where(buildSearchPredicate(searchCriteria, qCatalogItem));

        addOffsetAndLimitToQuery(offset, limit, query, orderBy, isDesc, qCatalogItem);

        List<T> fetch = query.fetch();
        return locale != null
                ? fetch.stream().peek(c -> {
                    c.setLocalizedPresentation(locale);
                    mapCatalogItemChildrenPresentation(c, locale);
                    }).collect(Collectors.toList())
                : fetch;
    }

    public Long countAll(EntityPath<? extends CatalogItem> entityPath) {
        QCatalogItem qCatalogItem = new QCatalogItem(entityPath);
        JPAQuery query = new JPAQueryFactory(entityManager).selectFrom(qCatalogItem);
        return query.fetchCount();
    }

    public Long countBySearchCriteria(EntityPath<? extends CatalogItem> entityPath, String searchCriteria) {
        QCatalogItem qCatalogItem = new QCatalogItem(entityPath);
        JPAQuery query = new JPAQueryFactory(entityManager)
                .selectFrom(qCatalogItem)
                .where(buildSearchPredicate(searchCriteria, qCatalogItem));
        return query.fetchCount();
    }

    public <T extends CatalogItem> T filterVisible(T catalogItem, String locale) {

        if (catalogItem == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        if (!catalogItem.isVisible()) {
            throw new WebApplicationException((Response.Status.FORBIDDEN));
        }

        catalogItem.setLocalizedPresentation(locale);
        mapCatalogItemChildrenPresentation(catalogItem, locale);

        return catalogItem;
    }

    private <T extends CatalogItem> void mapCatalogItemChildrenPresentation(T catalogItem, String locale) {

        if (locale == null) return;

        if (catalogItem instanceof Catalog) {
            ((Catalog) catalogItem).setRootCategories(((Catalog) catalogItem).getRootCategories().stream()
                    .peek(c -> c.setLocalizedPresentation(locale))
                    .collect(Collectors.toList()));

        } else if (catalogItem instanceof Category) {
            ((Category) catalogItem).setChildCategories(((Category) catalogItem).getChildCategories().stream()
                    .peek(c -> c.setLocalizedPresentation(locale))
                    .collect(Collectors.toList()));

        } else if (catalogItem instanceof Product) {
            ((Product) catalogItem).setChildSKUs(((Product) catalogItem).getChildSKUs().stream()
                    .peek(c -> c.setLocalizedPresentation(locale))
                    .collect(Collectors.toList()));

            ((Product) catalogItem).setDiscounts(((Product) catalogItem).getDiscounts().stream()
                    .peek(c -> c.setLocalizedPresentation(locale))
                    .collect(Collectors.toList()));
        }
    }

    private void addOffsetAndLimitToQuery(Integer offset, Integer limit, JPAQuery query, String orderBy, Boolean isDesc, QCatalogItem qCatalogItem) {
        if (offset != null)
            query.offset(offset);
        if (limit != null)
            query.limit(limit);

        sortBy(orderBy, isDesc, query, qCatalogItem);
    }

    private BooleanExpression buildSearchPredicate(String search, QCatalogItem qCatalogItem) {
        BooleanExpression searchPredicate = qCatalogItem.name.containsIgnoreCase(search)
                .or(qCatalogItem.description.containsIgnoreCase(search));

        if (NumberUtils.isNumber(search)) {
            Long searchId = Long.parseLong(search);
            searchPredicate = qCatalogItem.id.eq(searchId);
        }
        return searchPredicate;
    }

    private <T extends CatalogItem> void sortBy(String orderby, Boolean isDesc, JPAQuery<T> query, QCatalogItem qCatalogItem) {

        Map<String, ComparableExpressionBase<?>> sortProperties = new HashMap<String, ComparableExpressionBase<?>>() {{
            put("id", qCatalogItem.id);
            put("name", qCatalogItem.name);
            put("description", qCatalogItem.description);
            put("startDate", qCatalogItem.startDate);
            put("endDate", qCatalogItem.endDate);
            put("disabled", qCatalogItem.disabled);
        }};

        if (orderby != null && sortProperties.containsKey(orderby)) {
            if (isDesc) {
                query.orderBy(sortProperties.get(orderby).desc());
            } else {
                query.orderBy(sortProperties.get(orderby).asc());
            }
        }
    }

    public <T extends CatalogItem> T findOne(EntityPath<T> entityPath, Long itemId, SecurityContext securityIdentity) {

        QCatalogItem qCatalogItem = new QCatalogItem(entityPath);
        JPAQuery<T> query = new JPAQueryFactory(entityManager)
                .selectFrom(entityPath)
                .where(qCatalogItem.id.eq(itemId));

        if (securityIdentity.isUserInRole(JeeshopRoles.ADMIN)) {
            return query.fetchOne();

        } else if (securityIdentity.isUserInRole(JeeshopRoles.STORE_ADMIN)) {
            return query.where(qCatalogItem.owner.eq(securityIdentity.getUserPrincipal().getName()))
                    .fetchOne();
        }

        throw new WebApplicationException(Response.Status.FORBIDDEN);
    }
}
