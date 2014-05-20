package org.rembx.jeeshop.catalog;

import java.util.Date;
import java.util.List;

/**
 * Created by remi on 20/05/14.
 */
public class Category {

    private Integer id;

    private String name;

    private String description;

    private List<Category> childCategories;

    private List<Product> childProducts;

    private Date startDate;

    private Date endDate;

    private Boolean disabled;

    private Image thumbnail;

    private Image smallImage;

    private Image largeImage;

    private Video video;
}
