package org.rembx.jeeshop.catalog;

import java.util.Date;
import java.util.List;

/**
 * Created by remi on 20/05/14.
 */
public class Product {

    private Integer id;

    private String name;

    private List<Category> parentCategories;

    private List<StockUnit> childStockUnits;

    private Date startDate;

    private Date endDate;

    private Boolean disabled;

    private Promotion promotion;

    private Discount discount;

}
