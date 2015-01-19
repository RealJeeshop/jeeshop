package org.rembx.jeeshop.order;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.expr.ComparableExpressionBase;
import org.apache.commons.lang.math.NumberUtils;
import org.rembx.jeeshop.order.model.Order;
import org.rembx.jeeshop.order.model.OrderStatus;
import org.rembx.jeeshop.user.model.User;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.rembx.jeeshop.order.model.QOrder.order;

/**
 * User finder utility
 */
public class OrderFinder {

    @PersistenceContext(unitName = UserPersistenceUnit.NAME)
    private EntityManager entityManager;

    private Map<String, ComparableExpressionBase<?>> orderSortProperties = new HashMap<String, ComparableExpressionBase<?>>() {{
        put("status", order.status);
        put("creationDate", order.creationDate);
        put("updateDate", order.updateDate);
    }};


    public OrderFinder() {
    }

    public OrderFinder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Order> findAll(Integer offset, Integer limit, String orderby, Boolean isDesc) {
        JPAQuery query = new JPAQuery(entityManager).from(order);


        if (offset != null)
            query.offset(offset);
        if (limit != null)
            query.limit(limit);

        sortBy(orderby, isDesc, query);

        return query.list(order);

    }

    public Long countAll() {
        JPAQuery query = new JPAQuery(entityManager).from(order);
        return query.count();
    }

    public Long countUserCompletedOrders(User user) {
        return new JPAQuery(entityManager)
                .from(order)
                .where(
                        order.user.eq(user),
                        order.status.notIn(OrderStatus.CREATED, OrderStatus.CANCELLED, OrderStatus.RETURNED))
                .count();
    }

    public Long countBySearchCriteria(String searchCriteria) {
        JPAQuery query = new JPAQuery(entityManager)
                .from(order)
                .where(buildSearchPredicate(searchCriteria));
        return query.count();
    }

    public List<Order> findBySearchCriteria(String searchCriteria, Integer offset, Integer limit, String orderby, Boolean isDesc) {
        JPAQuery query = new JPAQuery(entityManager).from(order)
                .where(buildSearchPredicate(searchCriteria));

        if (offset != null)
            query.offset(offset);
        if (limit != null)
            query.limit(limit);

        sortBy(orderby, isDesc, query);

        return query.list(order);
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
        BooleanExpression searchExpression =  order.user.login.containsIgnoreCase(search)
                .or(order.user.firstname.containsIgnoreCase(search))
                .or(order.user.lastname.containsIgnoreCase(search))
                .or(order.transactionId.containsIgnoreCase(search));

        if (NumberUtils.isNumber(search)) {
            Long searchId = Long.parseLong(search);
            searchExpression = order.user.id.eq(searchId).or(order.transactionId.eq(search));
        }

        return searchExpression;
    }
}
