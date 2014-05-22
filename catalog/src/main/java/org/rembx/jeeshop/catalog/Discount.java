package org.rembx.jeeshop.catalog;

import java.util.Date;
import java.util.List;

/**
 * Created by remi on 20/05/14.
 */
public class Discount {

    private enum type {
        DISCOUNT_RATE,
        GIFT,
        ORDER_DISCOUNT_AMOUNT,
        SHIPPING_FEE_DISCOUNT_AMOUNT
    }

    private enum trigger{
        QUANTITY_OF_PRODUCTS,
        QUANTITY_OF_CATEGORY,
        ORDER_AMOUNT
    }

    private String name;

    private String voucherCode;

    private Promotion promotion;

    private Double amount;

    private Double discount;

    private List<StockUnit> gifts;

    private Integer usesPerCustomer;


    /**
     * Cannot be used with other discounts when true
     */
    private Boolean unique;

    private Date startDate;

    private Date endDate;

    private Boolean disabled;
}
