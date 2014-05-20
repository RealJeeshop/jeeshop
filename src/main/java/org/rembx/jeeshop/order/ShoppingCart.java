package org.rembx.jeeshop.order;

import org.rembx.jeeshop.catalog.StockUnit;

/**
 * Created by remi on 20/05/14.
 */
public interface ShoppingCart {
    void add(StockUnit stockUnit, Integer quantity);
    void remove(StockUnit stockUnit,Integer quantity);
    void checkout();
}
