package org.rembx.jeeshop.order.model;

import org.rembx.jeeshop.catalog.model.SKU;

/**
 * Created by remi on 20/05/14.
 */
public interface ShoppingCart {
    void add(SKU SKU, Integer quantity);
    void remove(SKU SKU,Integer quantity);
    void checkout();
}
