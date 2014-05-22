package org.rembx.jeeshop.catalog;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Currency;
import java.util.Set;

/**
 * Created by remi on 20/05/14.
 */
public class StockUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    private Product product;

    private Double price;

    @Size(min=3, max = 3)
    @Column(length = 3)
    private Currency currency;

    private Integer threshold;

    private Integer quantity;

    private Boolean disabled;

    @ManyToMany
    private Set<Promotion> promotions;

    /**
     * Calculated field true if quantity > threshold
     */
    private Boolean available;

}
