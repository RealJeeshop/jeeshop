package org.rembx.jeeshop.order;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class FeesTest {

    @Test
    public void getShippingFeesShouldReturnConfiguredShippingFeeAsDouble() throws Exception {
        OrderConfiguration orderConfiguration = orderConfiguration();

        Fees fees = new Fees(orderConfiguration);
        assertThat(fees.getShippingFee()).isEqualTo(11.0);
    }

    @Test
    public void getVATShouldReturnConfiguredVATAsDouble() throws Exception {

        OrderConfiguration orderConfiguration = orderConfiguration();

        Fees fees = new Fees(orderConfiguration);

        assertThat(fees.getShippingFee()).isEqualTo(11);

    }


    @Test
    public void getVATOrShippingFeeShouldReturnNullWhenNoMatchingOrderConfiguration() throws Exception {

        Fees fees = new Fees();

        assertThat(fees.getShippingFee()).isEqualTo(null);
        assertThat(fees.getVAT()).isEqualTo(null);


    }

    private OrderConfiguration orderConfiguration() {
        return new OrderConfiguration("11.0", "19.6");
    }
}