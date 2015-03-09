package org.rembx.jeeshop.catalog;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.expr.ComparableExpressionBase;
import com.mysema.query.types.expr.SimpleExpression;
import com.mysema.query.types.path.EntityPathBase;
import com.mysema.query.types.path.ListPath;
import org.apache.commons.lang.math.NumberUtils;
import org.rembx.jeeshop.catalog.model.CatalogItem;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.QCatalogItem;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                        qCatalogItem.endDate.after(now).or(qCatalogItem.endDate.isNull()),
                        qCatalogItem.startDate.before(now).or(qCatalogItem.startDate.isNull()),
                        qCatalogItem.in(items)
                )
                .list(entityPathBase);

        results.forEach((catalogItem) -> catalogItem.setLocalizedPresentation(locale));

        return results;

    }

    public <T extends CatalogItem> List<T> findAll(EntityPathBase<T> entityPathBase, Integer offset, Integer limit,String orderBy, Boolean isDesc) {
        QCatalogItem qCatalogItem = new QCatalogItem(entityPathBase);

        JPAQuery query = new JPAQuery(entityManager).from(qCatalogItem);

        addOffsetAndLimitToQuery(offset, limit, query, orderBy, isDesc, qCatalogItem);

        return query.list(entityPathBase);
    }


    public <T extends CatalogItem, P extends CatalogItem> List<P> findForeignHolder(EntityPathBase<P> hp,
                                                                                    ListPath<T, ? extends SimpleExpression<T>> h, T c) {

        return new JPAQuery(entityManager)
                .from(hp)
                .where(h.contains(c))
                .list(hp);
    }

    public <T extends CatalogItem> List<T> findBySearchCriteria(EntityPathBase<T> entityPathBase, String searchCriteria,
                                                                Integer offset, Integer limit, String orderBy, Boolean isDesc) {
        QCatalogItem qCatalogItem = new QCatalogItem(entityPathBase);

        JPAQuery query = new JPAQuery(entityManager).from(qCatalogItem)
                .where(buildSearchPredicate(searchCriteria, qCatalogItem));

        addOffsetAndLimitToQuery(offset, limit, query, orderBy, isDesc, qCatalogItem);

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

    private void sortBy(String orderby, Boolean isDesc, JPAQuery query, QCatalogItem qCatalogItem) {

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

}
