package org.rembx.jeeshop.order;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.expr.ComparableExpressionBase;
import org.rembx.jeeshop.order.model.Order;
import org.rembx.jeeshop.order.model.QOrder;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User finder utility
 */
public class OrderFinder {

    @PersistenceContext(unitName = UserPersistenceUnit.NAME)
    private EntityManager entityManager;

    private Map<String, ComparableExpressionBase<?>> orderSortProperties = new HashMap<String, ComparableExpressionBase<?>>() {{
        put("status", QOrder.order.status);
        put("creationDate", QOrder.order.creationDate);
        put("updateDate", QOrder.order.updateDate);
    }};


    public OrderFinder() {
    }

    public OrderFinder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Order> findAll(Integer offset, Integer limit, String orderby, Boolean isDesc) {
        JPAQuery query = new JPAQuery(entityManager).from(QOrder.order);


        if (offset != null)
            query.offset(offset);
        if (limit != null)
            query.limit(limit);

        sortBy(orderby, isDesc, query);

        return query.list(QOrder.order);

    }

    public Long countAll() {
        JPAQuery query = new JPAQuery(entityManager).from(QOrder.order);
        return query.count();
    }

    public Long countBySearchCriteria(String searchCriteria) {
        JPAQuery query = new JPAQuery(entityManager)
                .from(QOrder.order)
                .where(buildSearchPredicate(searchCriteria));
        return query.count();
    }

    public List<Order> findBySearchCriteria(String searchCriteria, Integer offset, Integer limit, String orderby, Boolean isDesc) {
        JPAQuery query = new JPAQuery(entityManager).from(QOrder.order)
                .where(buildSearchPredicate(searchCriteria));

        if (offset != null)
            query.offset(offset);
        if (limit != null)
            query.limit(limit);

        sortBy(orderby, isDesc, query);

        return query.list(QOrder.order);
    }

    private void sortBy(String orderby, Boolean isDesc, JPAQuery query) {
        if (orderby != null && orderSortProperties.containsKey(orderby)) {
            if (isDesc) {
                query.orderBy(orderSortProperties.get(orderby).desc());
            } else {
                query.orderBy(orderSortProperties.get(orderby).asc());
            }
        }
    }

    private BooleanExpression buildSearchPredicate(String search) {
        return QOrder.order.user.login.containsIgnoreCase(search)
                .or(QOrder.order.user.firstname.containsIgnoreCase(search))
                .or(QOrder.order.user.lastname.containsIgnoreCase(search));
    }
}
