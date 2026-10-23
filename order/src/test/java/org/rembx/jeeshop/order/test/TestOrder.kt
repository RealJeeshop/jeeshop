package org.rembx.jeeshop.order.test

import com.google.common.collect.Sets
import jakarta.persistence.Persistence
import org.rembx.jeeshop.order.domain.Order
import org.rembx.jeeshop.order.domain.OrderItem
import org.rembx.jeeshop.order.domain.OrderStatus
import org.rembx.jeeshop.user.domain.Address
import org.rembx.jeeshop.user.domain.User
import org.rembx.jeeshop.user.infrastructure.repository.UserPersistenceUnit
import org.rembx.jeeshop.user.test.TestUser

class TestOrder private constructor() {
    fun firstOrder(): Order = order1

    fun secondOrder(): Order = order2

    fun firstOrdersUser(): User = checkNotNull(order1.customer)

    companion object {
        private var instance: TestOrder? = null
        private lateinit var order1: Order
        private lateinit var order2: Order

        @JvmStatic
        fun getInstance(): TestOrder {
            instance?.let { return it }
            val testUser = TestUser.getInstance()
            val entityManager = Persistence.createEntityManagerFactory(UserPersistenceUnit.NAME).createEntityManager()
            entityManager.transaction.begin()

            val deliveryAddress = Address("21 Blue street", "Chicago", "78801", "John", "Doe", "M.", null, "FRA")
            val billingAddress = Address("53 Green street", "Chicago", "78801", "John", "Doe", "M.", null, "FRA")
            entityManager.persist(deliveryAddress)
            entityManager.persist(billingAddress)

            order1 = Order(testUser.firstUser(), null, deliveryAddress, billingAddress, OrderStatus.PAYMENT_VALIDATED)
            order1.storeId = 1L
            val orderItem1 = OrderItem(1L, 1L, 2).apply { order = order1 }
            order1.items = Sets.newHashSet(orderItem1)
            entityManager.persist(order1)

            order2 = Order(testUser.firstUser(), null, null, null, OrderStatus.CREATED).apply { storeId = 1L }
            entityManager.persist(order2)
            entityManager.transaction.commit()

            return TestOrder().also {
                instance = it
                entityManager.close()
            }
        }
    }
}
