package org.rembx.jeeshop.order;

import org.rembx.jeeshop.configuration.NamedConfiguration;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class OrderConfiguration {

    @Inject
    @NamedConfiguration("fixed.delivery.fee")
    private String fixedDeliveryFee;

    @Inject
    @NamedConfiguration("vat")
    private String vat;

    public OrderConfiguration() {
    }

    public OrderConfiguration(String fixedDeliveryFee, String vat) {
        this.fixedDeliveryFee = fixedDeliveryFee;
        this.vat = vat;
    }

    public Double getFixedDeliveryFee() {
        return Double.parseDouble(fixedDeliveryFee);
    }

    public Double getVAT() {
        return Double.parseDouble(vat);
    }
}
