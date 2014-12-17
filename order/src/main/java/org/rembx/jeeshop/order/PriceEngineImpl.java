package org.rembx.jeeshop.order;

import org.apache.commons.collections.CollectionUtils;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.SKU;
import org.rembx.jeeshop.order.model.Order;
import org.rembx.jeeshop.order.model.OrderItem;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Order price engine
 * Computes order's price
 */
public class PriceEngineImpl implements PriceEngine {

    @PersistenceContext(unitName = CatalogPersistenceUnit.NAME)
    private EntityManager entityManager;

    @Inject
    private OrderConfiguration orderConfiguration;

    public PriceEngineImpl() {

    }

    public PriceEngineImpl(EntityManager entityManager, OrderConfiguration orderConfiguration) {
        this.entityManager = entityManager;
        this.orderConfiguration = orderConfiguration;
    }

    @Override
    public Double computePrice(Order order) {

        if (CollectionUtils.isEmpty(order.getItems())){
            throw new IllegalStateException("Order items list is empty "+order);
        }

        Double price = 0.0;

        for (OrderItem orderItem : order.getItems()){

            SKU sku = entityManager.find(SKU.class,orderItem.getSkuId());
            price += (sku.getPrice()*orderItem.getQuantity());

        }

        final Double fixedDeliveryFee = orderConfiguration.getFixedDeliveryFee();
        if (fixedDeliveryFee != null){
            price += fixedDeliveryFee;
        }

        return price;

    }
}
