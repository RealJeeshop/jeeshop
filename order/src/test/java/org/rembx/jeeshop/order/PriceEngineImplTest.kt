package org.rembx.jeeshop.order

import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.rembx.jeeshop.catalog.infrastructure.repository.DiscountRepositoryImpl
import org.rembx.jeeshop.catalog.domain.SKU
import org.rembx.jeeshop.order.domain.Order
import org.rembx.jeeshop.order.domain.OrderItem
import org.rembx.jeeshop.order.domain.OrderRepository

class PriceEngineImplTest {
    private lateinit var entityManager: EntityManager
    private lateinit var orderConfiguration: OrderConfiguration
    private lateinit var orderRepository: OrderRepository
    private lateinit var discountFinder: DiscountRepositoryImpl
    private lateinit var orderPriceEngine: PriceEngineImpl

    @BeforeEach
    fun setup() {
        entityManager = mock(EntityManager::class.java)
        orderConfiguration = mock(OrderConfiguration::class.java)
        orderRepository = mock(OrderRepository::class.java)
        discountFinder = mock(DiscountRepositoryImpl::class.java)
        orderPriceEngine = PriceEngineImpl(entityManager, orderConfiguration,         orderRepository, discountFinder)
    }

    @Test
    fun computePrice_ShouldAddPriceOfEachOrderItemsMultipliedByQuantityWithFixedDeliveryFee() {
        val sku = SKU().apply { price = 10.0 }
        val sku2 = SKU().apply { price = 20.0 }
        `when`(entityManager.find(SKU::class.java, 1L)).thenReturn(sku)
        `when`(entityManager.find(SKU::class.java, 2L)).thenReturn(sku2)
        `when`(orderConfiguration.getFixedDeliveryFee()).thenReturn(11.0)
        val order = Order().apply {
            items = hashSetOf(OrderItem(1L, 1L, 1), OrderItem(2L, 2L, 2))
        }

        orderPriceEngine.computePrice(order)

        verify(entityManager).find(SKU::class.java, 1L)
        verify(entityManager).find(SKU::class.java, 2L)
        verify(orderConfiguration).getFixedDeliveryFee()
        assertThat(order.price).isEqualTo(61.0)
    }

    @Test
    fun computePrice_ShouldAddPriceOfEachOrderItemsMultipliedByQuantity() {
        val sku = SKU().apply { price = 10.0 }
        val sku2 = SKU().apply { price = 20.0 }
        `when`(entityManager.find(SKU::class.java, 1L)).thenReturn(sku)
        `when`(entityManager.find(SKU::class.java, 2L)).thenReturn(sku2)
        doReturn(null).`when`(orderConfiguration).getFixedDeliveryFee()
        val order = Order().apply {
            items = hashSetOf(OrderItem(1L, 1L, 1), OrderItem(2L, 2L, 2))
        }

        orderPriceEngine.computePrice(order)

        verify(entityManager).find(SKU::class.java, 1L)
        verify(entityManager).find(SKU::class.java, 2L)
        verify(orderConfiguration).getFixedDeliveryFee()
        assertThat(order.price).isEqualTo(50.0)
        for (item in order.items!!) {
            if (item.skuId == 1L) assertThat(item.price).isEqualTo(10.0)
            else assertThat(item.price).isEqualTo(20.0)
        }
    }

    @Test
    fun computePrice_ShouldThrowEx_whenOrderHasNoItems() {
        val order = Order()
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException::class.java) {
            orderPriceEngine.computePrice(order)
        }
    }
}
