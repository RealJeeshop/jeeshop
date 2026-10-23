package org.rembx.jeeshop.order.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.text.SimpleDateFormat

class OrderTest {
    @Test
    fun testPrePersist() {
        val order = Order()
        assertThat(order.updateDate).isNull()
        order.prePersist()
        assertThat(order.updateDate).isNotNull()
    }

    @Test
    fun testPreUpdate() {
        val order = Order()
        assertThat(order.creationDate).isNull()
        assertThat(order.updateDate).isNull()
        order.prePersist()
        assertThat(order.creationDate).isNotNull()
        assertThat(order.updateDate).isNotNull()
    }

    @Test
    fun computeOrderReference_orderWithoutTransactionId() {
        val order = Order()
        order.id = 1L
        order.creationDate = SimpleDateFormat("MMddyyyy").parse("11222222")
        assertThat(order.reference).isNull()
        order.computeOrderReference()
        assertThat(order.reference).isEqualTo("11222222-1")
    }

    @Test
    fun computeOrderReference_orderWithTransactionId() {
        val order = Order()
        order.id = 1L
        order.creationDate = SimpleDateFormat("MMddyyyy").parse("11222222")
        order.transactionId = "1234"
        order.computeOrderReference()
        assertThat(order.reference).isEqualTo("11222222-1-1234")
    }
}
