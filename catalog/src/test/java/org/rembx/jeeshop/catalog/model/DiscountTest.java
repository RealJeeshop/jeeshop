package org.rembx.jeeshop.catalog.model;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.rembx.jeeshop.catalog.model.Discount.ApplicableTo.ORDER;
import static org.rembx.jeeshop.catalog.model.Discount.Trigger.AMOUNT;
import static org.rembx.jeeshop.catalog.model.Discount.Type.DISCOUNT_RATE;
import static org.rembx.jeeshop.catalog.model.Discount.Type.ORDER_DISCOUNT_AMOUNT;
import static org.rembx.jeeshop.catalog.model.Discount.Type.SHIPPING_FEE_DISCOUNT_AMOUNT;

public class DiscountTest {

    @Test
    public void processAmountDiscountWithRate() throws Exception {
        Discount discount = new Discount("discount1", "a discount", ORDER, DISCOUNT_RATE, AMOUNT, null,10.0, 2.0, 1, true,null,null, false);
        assertThat(discount.processDiscount(10.0,10.0)).isEqualTo(9);
    }

    @Test
    public void processAmountDiscountWithRate_andCurrentPriceDifferentFromOriginalPrice() throws Exception {
        Discount discount = new Discount("discount1", "a discount", ORDER, DISCOUNT_RATE, AMOUNT, null,10.0, 2.0, 1, true,null,null, false);
        assertThat(discount.processDiscount(10.0,20.0)).isEqualTo(8);
    }

    @Test
    public void processAmountDiscountWithDiscountValue() throws Exception {
        Discount discount = new Discount("discount1", "a discount", ORDER, ORDER_DISCOUNT_AMOUNT, AMOUNT, null,5.0, 2.0, 1, true,null,null, false);
        assertThat(discount.processDiscount(10.0,10.0)).isEqualTo(5);
    }

    @Test
    public void processShippingDiscount() throws Exception {
        Discount discount = new Discount("discount1", "a discount", ORDER, SHIPPING_FEE_DISCOUNT_AMOUNT, AMOUNT, null,5.0, 2.0, 1, true,null,null, false);
        assertThat(discount.processDiscount(10.0,10.0)).isEqualTo(5);
    }

    @Test
    public void isEligible_givenItemsPriceLowerThanTriggerValueAndAmountTrigger_shouldReturnFalse_() throws Exception{
        Discount discount = new Discount("discount1", "a discount", ORDER, DISCOUNT_RATE, AMOUNT, null, 0.1, 2.0, 1, true,null,null, false);
        assertThat(discount.isEligible(1.9)).isFalse();
    }

    @Test
    public void isEligible_givenItemsPriceGreaterOrEqualThanTriggerValueAndAmountTrigger_shouldReturnTrue_() throws Exception{
        Discount discount = new Discount("discount1", "a discount", ORDER, DISCOUNT_RATE, AMOUNT, null, 0.1, 2.0, 1, true,null,null, false);
        assertThat(discount.isEligible(2.0)).isTrue();
    }

    @Test
    public void isEligible_WhenDiscountHasNullTriggerValue_shouldReturnTrue_() throws Exception{
        Discount discount = new Discount("discount1", "a discount", ORDER, DISCOUNT_RATE, AMOUNT, null, 0.1, null, 1, true,null,null, false);
        assertThat(discount.isEligible(2.0)).isTrue();
    }
}