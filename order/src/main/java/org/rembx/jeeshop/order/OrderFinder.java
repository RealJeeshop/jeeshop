package org.rembx.jeeshop.order;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.expr.ComparableExpressionBase;
import org.apache.commons.lang.math.NumberUtils;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Discount;
import org.rembx.jeeshop.catalog.model.Product;
import org.rembx.jeeshop.catalog.model.SKU;
import org.rembx.jeeshop.order.model.Order;
import org.rembx.jeeshop.order.model.OrderStatus;
import org.rembx.jeeshop.user.model.User;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.rembx.jeeshop.order.model.QOrder.order;

/**
 * User finder utility
 */
public class OrderFinder {

    private final static Logger LOG = LoggerFactory.getLogger(OrderFinder.class);

    @Inject
    private OrderConfiguration orderConfiguration;

    @PersistenceContext(unitName = UserPersistenceUnit.NAME)
    private EntityManager entityManager;

    @PersistenceContext (unitName = CatalogPersistenceUnit.NAME)
    private EntityManager catalogEntityManager;

    private static final Map<String, ComparableExpressionBase<?>> orderSortProperties = new HashMap<String, ComparableExpressionBase<?>>() {{
        put("id", order.id);
        put("status", order.status);
        put("creationDate", order.creationDate);
        put("updateDate", order.updateDate);
    }};


    public OrderFinder() {
    }

    public OrderFinder(EntityManager entityManager, EntityManager catalogEntityManager, OrderConfiguration orderConfiguration) {
        this.entityManager = entityManager;
        this.catalogEntityManager = catalogEntityManager;
        this.orderConfiguration = orderConfiguration;
    }



    public Long countUserCompletedOrders(User user) {
        return new JPAQuery(entityManager)
                .from(order)
                .where(
                        order.user.eq(user),
                        order.status.notIn(OrderStatus.CREATED, OrderStatus.CANCELLED, OrderStatus.RETURNED))
                .count();
    }

    public Long countAll(String searchCriteria, OrderStatus status, Long skuId) {
        JPAQuery query = new JPAQuery(entityManager).from(order)
                .where(matchesSearchAndStatusAndItemsSkuId(searchCriteria, status, skuId));
        return query.count();
    }

    public List<Order> findAll(Integer offset, Integer limit, String orderby, Boolean isDesc, String searchCriteria, OrderStatus status,Long skuId, boolean enhanceResult) {
        JPAQuery query = new JPAQuery(entityManager).from(order)
                .where(matchesSearchAndStatusAndItemsSkuId(searchCriteria, status, skuId));

        if (offset != null)
            query.offset(offset);
        if (limit != null)
            query.limit(limit);

        sortBy(orderby, isDesc, query);

        List<Order> orders =  query.list(order);

        if (enhanceResult)
            orders.forEach(this::enhanceOrder);

        return orders;

    }

    private BooleanExpression matchesSearchAndStatusAndItemsSkuId(String searchCriteria, OrderStatus status, Long skuId) {
        BooleanExpression expression = null;

        if (searchCriteria != null)
            expression = matchesSearchCriteria(searchCriteria);

        if (status != null){
            expression = expression != null? expression.and(order.status.eq(status)):order.status.eq(status);
        }

        if (skuId != null){
            expression = expression != null? expression.and(order.items.any().skuId.eq(skuId)):order.items.any().skuId.eq(skuId);
        }
        return expression;
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

    private BooleanExpression matchesSearchCriteria(String search) {
        BooleanExpression searchExpression =  order.user.login.containsIgnoreCase(search)
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
            try {
                if (product.getLocalizedPresentation()!= null)
                    orderItem.setPresentationImageURI(new URI("products/" + orderItem.getProductId() + "/" + product.getLocalizedPresentation().getLocale() + "/"+product.getLocalizedPresentation().getSmallImage().getUri()));
            } catch (URISyntaxException e) {
                LOG.error("Error while building image path for item "+orderItem.getId(), e);
            }
        });

        order.getOrderDiscounts().forEach(orderDiscount -> {
            Discount discount = catalogEntityManager.find(Discount.class, orderDiscount.getDiscountId());
            discount.setLocalizedPresentation(user.getPreferredLocale());
            orderDiscount.setDisplayName(discount.getLocalizedPresentation().getDisplayName()!=null?discount.getLocalizedPresentation().getDisplayName():discount.getName());
            orderDiscount.setRateType(discount.getRateType());

            try {
                if (discount.getLocalizedPresentation()!=null)
                    orderDiscount.setPresentationImageURI(new URI("discounts/" + orderDiscount.getDiscountId() + "/" + discount.getLocalizedPresentation().getLocale() + "/"+discount.getLocalizedPresentation().getSmallImage().getUri()));
            } catch (URISyntaxException e) {
                LOG.error("Error while building discount path for orderDiscount with discountId "+orderDiscount.getDiscountId(), e);
            }
        });

        order.setDeliveryFee(orderConfiguration.getFixedDeliveryFee());
    }
}
