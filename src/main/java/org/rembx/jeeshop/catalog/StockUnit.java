package org.rembx.jeeshop.catalog;

import java.util.Currency;

/**
 * Created by remi on 20/05/14.
 */
public class StockUnit {
    private Integer id;

    private Product product;

    private Double price;

    private Currency currency;

    private String threshold;

    private Integer quantity;

    private Boolean disabled;

    private Promotion promotion;

    /**
     * Calculated field true if quantity > threshold
     */
    private Boolean available;

}
