package org.rembx.jeeshop.catalog.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.rembx.jeeshop.catalog.domain.Discount

class DiscountTest {
    @Test
    @Throws(Exception::class)
    fun processAmountDiscountWithRate() {
        val discount = Discount(
            "discount1",
            "a discount",
            Discount.ApplicableTo.ORDER,
            Discount.Type.DISCOUNT_RATE,
            Discount.Trigger.AMOUNT,
            null,
            10.0,
            2.0,
            1,
            true,
            null,
            null,
            false,
            "test@test.com"
        )
        assertThat(discount.processDiscount(10.0, 10.0)).isEqualTo(9)
    }

    @Test
    @Throws(Exception::class)
    fun processAmountDiscountWithRate_andCurrentPriceDifferentFromOriginalPrice() {
        val discount = Discount(
            "discount1",
            "a discount",
            Discount.ApplicableTo.ORDER,
            Discount.Type.DISCOUNT_RATE,
            Discount.Trigger.AMOUNT,
            null,
            10.0,
            2.0,
            1,
            true,
            null,
            null,
            false,
            "test@test.com"
        )
        assertThat(discount.processDiscount(10.0, 20.0)).isEqualTo(8)
    }

    @Test
    @Throws(Exception::class)
    fun processAmountDiscountWithDiscountValue() {
        val discount = Discount(
            "discount1",
            "a discount",
            Discount.ApplicableTo.ORDER,
            Discount.Type.ORDER_DISCOUNT_AMOUNT,
            Discount.Trigger.AMOUNT,
            null,
            5.0,
            2.0,
            1,
            true,
            null,
            null,
            false,
            "test@test.com"
        )
        assertThat(discount.processDiscount(10.0, 10.0)).isEqualTo(5)
    }

    @Test
    @Throws(Exception::class)
    fun processShippingDiscount() {
        val discount = Discount(
            "discount1",
            "a discount",
            Discount.ApplicableTo.ORDER,
            Discount.Type.SHIPPING_FEE_DISCOUNT_AMOUNT,
            Discount.Trigger.AMOUNT,
            null,
            5.0,
            2.0,
            1,
            true,
            null,
            null,
            false,
            "test@test.com"
        )
        assertThat(discount.processDiscount(10.0, 10.0)).isEqualTo(5)
    }

    @Test
    @Throws(Exception::class)
    fun isEligible_givenItemsPriceLowerThanTriggerValueAndAmountTrigger_shouldReturnFalse_() {
        val discount = Discount(
            "discount1",
            "a discount",
            Discount.ApplicableTo.ORDER,
            Discount.Type.DISCOUNT_RATE,
            Discount.Trigger.AMOUNT,
            null,
            0.1,
            2.0,
            1,
            true,
            null,
            null,
            false,
            "test@test.com"
        )
        assertThat(discount.isEligible(1.9)).isFalse()
    }

    @Test
    @Throws(Exception::class)
    fun isEligible_givenItemsPriceGreaterOrEqualThanTriggerValueAndAmountTrigger_shouldReturnTrue_() {
        val discount = Discount(
            "discount1",
            "a discount",
            Discount.ApplicableTo.ORDER,
            Discount.Type.DISCOUNT_RATE,
            Discount.Trigger.AMOUNT,
            null,
            0.1,
            2.0,
            1,
            true,
            null,
            null,
            false,
            "test@test.com"
        )
        assertThat(discount.isEligible(2.0)).isTrue()
    }

    @Test
    @Throws(Exception::class)
    fun isEligible_WhenDiscountHasNullTriggerValue_shouldReturnTrue_() {
        val discount = Discount(
            "discount1",
            "a discount",
            Discount.ApplicableTo.ORDER,
            Discount.Type.DISCOUNT_RATE,
            Discount.Trigger.AMOUNT,
            null,
            0.1,
            null,
            1,
            true,
            null,
            null,
            false,
            "test@test.com"
        )
        assertThat(discount.isEligible(2.0)).isTrue()
    }
}
