package org.rembx.jeeshop.catalog;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.path.EntityPathBase;
import org.apache.commons.lang.math.NumberUtils;
import org.rembx.jeeshop.catalog.model.CatalogItem;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.QCatalogItem;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

/**
 * Utility class for common finders on CatalogItem entities
 */
public class CatalogItemFinder {
    @PersistenceContext(unitName = CatalogPersistenceUnit.NAME)
    private EntityManager entityManager;

    public CatalogItemFinder() {
    }

    public CatalogItemFinder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public <T extends CatalogItem> List<T> findVisibleCatalogItems(EntityPathBase<T> entityPathBase, List<T> items, String locale) {
        QCatalogItem qCatalogItem = new QCatalogItem(entityPathBase);
        Date now = new Date();
        List<T> results = new JPAQuery(entityManager)
                .from(qCatalogItem).where(
                        qCatalogItem.disabled.isFalse(),
                        qCatalogItem.endDate.after(now),
                        qCatalogItem.startDate.before(now),
                        qCatalogItem.in(items)
                )
                .list(entityPathBase);

        results.forEach((catalogItem) -> catalogItem.setLocalizedPresentation(locale));

        return results;

    }

    public <T extends CatalogItem> List<T> findAll(EntityPathBase<T> entityPathBase, Integer offset, Integer limit) {
        QCatalogItem qCatalogItem = new QCatalogItem(entityPathBase);

        JPAQuery query = new JPAQuery(entityManager).from(qCatalogItem);

        addOffsetAndLimitToQuery(offset, limit, query);

        return query.list(entityPathBase);
    }

    public <T extends CatalogItem> List<T> findBySearchCriteria(EntityPathBase<T> entityPathBase, String searchCriteria, Integer offset, Integer limit) {
        QCatalogItem qCatalogItem = new QCatalogItem(entityPathBase);

        JPAQuery query = new JPAQuery(entityManager).from(qCatalogItem)
                .where(buildSearchPredicate(searchCriteria, qCatalogItem));

        addOffsetAndLimitToQuery(offset, limit, query);

        return query.list(entityPathBase);
    }

    public Long countAll(EntityPathBase<? extends CatalogItem> entityPathBase) {
        QCatalogItem qCatalogItem = new QCatalogItem(entityPathBase);
        JPAQuery query = new JPAQuery(entityManager).from(qCatalogItem);
        return query.count();
    }

    public Long countBySearchCriteria(EntityPathBase<? extends CatalogItem> entityPathBase, String searchCriteria) {
        QCatalogItem qCatalogItem = new QCatalogItem(entityPathBase);
        JPAQuery query = new JPAQuery(entityManager)
                .from(qCatalogItem)
                .where(buildSearchPredicate(searchCriteria, qCatalogItem));
        return query.count();
    }

    private void addOffsetAndLimitToQuery(Integer offset, Integer limit, JPAQuery query) {
        if (offset != null)
            query.offset(offset);
        if (limit != null)
            query.limit(limit);
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

    public <T extends CatalogItem> T filterVisible(T catalogItem, String locale) {

        if (catalogItem == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        if (!catalogItem.isVisible()) {
            throw new WebApplicationException((Response.Status.FORBIDDEN));
        }

        catalogItem.setLocalizedPresentation(locale);

        return catalogItem;
    }

}
