package org.rembx.jeeshop.catalog;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by remi on 20/05/14.
 */
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique = true, nullable = false, length = 50)
    @Size(max = 50)
    @NotNull
    private String name;

    @OneToMany
    private List<StockUnit> childStockUnits;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    private Boolean disabled;

    @ManyToMany
    private Set<Promotion> promotions;

    @OneToMany
    @OrderColumn(name="index")
    private List <Discount> discounts;

}
