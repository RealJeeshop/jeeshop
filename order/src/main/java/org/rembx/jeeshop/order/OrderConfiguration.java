package org.rembx.jeeshop.order;

import org.rembx.jeeshop.configuration.NamedConfiguration;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class OrderConfiguration {

    @Inject
    @NamedConfiguration("fixed.delivery.fee")
    private String fixedDeliveryFee;

    public Double getFixedDeliveryFee() {
        return Double.parseDouble(fixedDeliveryFee);
    }
}
