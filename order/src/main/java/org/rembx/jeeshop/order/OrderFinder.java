package org.rembx.jeeshop.order;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.quarkus.hibernate.orm.PersistenceUnit;
import org.apache.commons.lang.math.NumberUtils;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Discount;
import org.rembx.jeeshop.catalog.model.Product;
import org.rembx.jeeshop.catalog.model.SKU;
import org.rembx.jeeshop.order.model.Order;
import org.rembx.jeeshop.order.model.OrderStatus;
import org.rembx.jeeshop.user.model.User;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.rembx.jeeshop.order.model.QOrder.order;

@ApplicationScoped
public class OrderFinder {

    private EntityManager entityManager;
    private EntityManager catalogEntityManager;
    private OrderConfiguration orderConfiguration;

    OrderFinder(@PersistenceUnit(UserPersistenceUnit.NAME) EntityManager entityManager,
                @PersistenceUnit(CatalogPersistenceUnit.NAME) EntityManager catalogEntityManager,
                OrderConfiguration orderConfiguration) {
        this.entityManager = entityManager;
        this.catalogEntityManager = catalogEntityManager;
        this.orderConfiguration = orderConfiguration;
    }

    private static final Map<String, ComparableExpressionBase<?>> sortProperties = new HashMap<>() {{
        put("id", order.id);
        put("owner", order.user.lastname);
        put("login", order.user.login);
        put("status", order.status);
        put("creationDate", order.creationDate);
        put("updateDate", order.updateDate);
    }};

    public Long countUserCompletedOrders(User user) {
        return new JPAQueryFactory(entityManager)
                .selectFrom(order)
                .where(
                        order.user.eq(user),
                        order.status.notIn(OrderStatus.CREATED, OrderStatus.CANCELLED, OrderStatus.RETURNED))
                .fetchCount();
    }

    public Long countAll(String searchCriteria, OrderStatus status, Long skuId) {
        JPAQuery<Order> query = new JPAQueryFactory(entityManager).selectFrom(order)
                .where(matchesSearchAndStatusAndItemsSkuId(searchCriteria, status, skuId));
        return query.fetchCount();
    }

    public List<Order> findAll(Integer offset, Integer limit, String orderby, Boolean isDesc, String searchCriteria, OrderStatus status, Long skuId, boolean enhanceResult) {
        JPAQuery<Order> query = new JPAQueryFactory(entityManager).selectFrom(order)
                .where(matchesSearchAndStatusAndItemsSkuId(searchCriteria, status, skuId));

        if (offset != null)
            query.offset(offset);
        if (limit != null)
            query.limit(limit);

        sortBy(orderby, isDesc, query);

        List<Order> orders = query.fetch();

        if (enhanceResult)
            orders.forEach(this::enhanceOrder);

        return orders;

    }

    public List<Order> findByUser(User user, Integer offset, Integer limit, String orderby, Boolean isDesc, OrderStatus status) {
        JPAQuery<Order> query = new JPAQueryFactory(entityManager).selectFrom(order)
                .where(
                        order.user.eq(user),
                        status != null && status.equals(OrderStatus.CREATED) ? null : order.status.ne(OrderStatus.CREATED),
                        status != null ? order.status.eq(status) : null);

        if (offset != null)
            query.offset(offset);
        if (limit != null)
            query.limit(limit);

        sortBy(orderby, isDesc, query);

        return query.fetch();
    }

    private BooleanExpression matchesSearchAndStatusAndItemsSkuId(String searchCriteria, OrderStatus status, Long skuId) {
        BooleanExpression expression = null;

        if (searchCriteria != null)
            expression = matchesSearchCriteria(searchCriteria);

        if (status != null) {
            expression = expression != null ? expression.and(order.status.eq(status)) : order.status.eq(status);
        }

        if (skuId != null) {
            expression = expression != null ? expression.and(order.items.any().skuId.eq(skuId)) : order.items.any().skuId.eq(skuId);
        }
        return expression;
    }


    private void sortBy(String orderby, Boolean isDesc, JPAQuery<Order> query) {
        if (orderby != null && sortProperties.containsKey(orderby)) {
            if (isDesc) {
                query.orderBy(sortProperties.get(orderby).desc());
            } else {
                query.orderBy(sortProperties.get(orderby).asc());
            }
        }
    }

    private BooleanExpression matchesSearchCriteria(String search) {
        BooleanExpression searchExpression = order.user.login.containsIgnoreCase(search)
                .or(order.user.firstname.containsIgnoreCase(search))
                .or(order.user.lastname.containsIgnoreCase(search))
                .or(order.transactionId.containsIgnoreCase(search));

        if (NumberUtils.isNumber(search)) {
            Long searchId = Long.parseLong(search);
            searchExpression = order.id.eq(searchId).or(order.transactionId.eq(search));
        }


        return searchExpression;
    }


    /**
     * Enhance given order with Catalog items data and order static configuration.
     *
     * @param order the order to enhance
     */
    public void enhanceOrder(Order order) {
        User user = order.getUser();
        order.getItems().forEach(orderItem -> {
            Product product = catalogEntityManager.find(Product.class, orderItem.getProductId());
            SKU sku = catalogEntityManager.find(SKU.class, orderItem.getSkuId());
            product.setLocalizedPresentation(user.getPreferredLocale());
            orderItem.setDisplayName(product.getLocalizedPresentation() != null ? product.getLocalizedPresentation().getDisplayName() : product.getName());
            orderItem.setSkuReference(sku.getReference());
            if (product.getLocalizedPresentation() != null && product.getLocalizedPresentation().getSmallImage() != null)
                orderItem.setPresentationImageURI("products/" + orderItem.getProductId() + "/" + product.getLocalizedPresentation().getLocale() + "/" + product.getLocalizedPresentation().getSmallImage().getUri());
        });

        order.getOrderDiscounts().forEach(orderDiscount -> {
            Discount discount = catalogEntityManager.find(Discount.class, orderDiscount.getDiscountId());
            discount.setLocalizedPresentation(user.getPreferredLocale());
            orderDiscount.setDisplayName(discount.getLocalizedPresentation().getDisplayName() != null ? discount.getLocalizedPresentation().getDisplayName() : discount.getName());
            orderDiscount.setRateType(discount.getRateType());

            if (discount.getLocalizedPresentation() != null && discount.getLocalizedPresentation().getSmallImage() != null)
                orderDiscount.setPresentationImageURI("discounts/" + orderDiscount.getDiscountId() + "/" + discount.getLocalizedPresentation().getLocale() + "/" + discount.getLocalizedPresentation().getSmallImage().getUri());
        });

        order.setDeliveryFee(orderConfiguration.getFixedDeliveryFee());
        order.setVat(orderConfiguration.getVAT());
    }
}
