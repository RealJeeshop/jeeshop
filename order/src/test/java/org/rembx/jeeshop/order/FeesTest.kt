package org.rembx.jeeshop.order

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FeesTest {
    @Test
    fun getShippingFeesShouldReturnConfiguredShippingFeeAsDouble() {
        val fees = Fees(orderConfiguration())
        assertThat(fees.getShippingFee()).isEqualTo(11.0)
    }

    @Test
    fun getVATShouldReturnConfiguredVATAsDouble() {
        val fees = Fees(orderConfiguration())
        assertThat(fees.getVAT()).isEqualTo(19.6)
    }

    @Test
    fun getVATOrShippingFeeShouldReturnNullWhenNoMatchingOrderConfiguration() {
        val fees = Fees(null)
        assertThat(fees.getShippingFee()).isEqualTo(null)
        assertThat(fees.getVAT()).isEqualTo(null)
    }

    private fun orderConfiguration() = OrderConfiguration("11.0", "19.6")
}
