package org.rembx.jeeshop.order

import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.apache.http.auth.BasicUserPrincipal
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.rembx.jeeshop.catalog.infrastructure.repository.CatalogItemRepositoryImpl
import org.rembx.jeeshop.catalog.infrastructure.repository.CatalogPersistenceUnit
import org.rembx.jeeshop.catalog.test.TestCatalog
import org.rembx.jeeshop.mail.Mailer
import org.rembx.jeeshop.order.domain.Order
import org.rembx.jeeshop.order.domain.OrderItem
import org.rembx.jeeshop.order.domain.OrderStatus
import org.rembx.jeeshop.order.infrastructure.api.Orders
import org.rembx.jeeshop.order.infrastructure.repository.OrderRepositoryImpl
import org.rembx.jeeshop.order.test.TestOrder
import org.rembx.jeeshop.rest.WebApplicationException
import org.rembx.jeeshop.role.JeeshopRoles
import org.rembx.jeeshop.user.infrastructure.repository.MailTemplateRepositoryImpl
import org.rembx.jeeshop.user.infrastructure.repository.UserRepositoryImpl
import org.rembx.jeeshop.user.domain.Address
import org.rembx.jeeshop.user.domain.User
import org.rembx.jeeshop.user.infrastructure.repository.UserPersistenceUnit
import org.rembx.jeeshop.user.test.TestUser

class OrdersCT {
    private lateinit var entityManager: EntityManager
    private lateinit var catalogEntityManager: EntityManager
    private lateinit var testOrder: TestOrder
    private lateinit var testCatalog: TestCatalog
    private lateinit var testUser: TestUser
    private lateinit var sessionContextMock: SecurityContext
    private lateinit var priceEngineMock: PriceEngine
    private lateinit var paymentTransactionEngine: PaymentTransactionEngine
    private lateinit var service: Orders
    private lateinit var catalogItemFinder: CatalogItemRepositoryImpl

    companion object {
        private lateinit var emf: EntityManagerFactory
        private lateinit var catalogEmf: EntityManagerFactory

        @JvmStatic
        @org.junit.jupiter.api.BeforeAll
        fun beforeClass() {
            emf = Persistence.createEntityManagerFactory(UserPersistenceUnit.NAME)
            catalogEmf = Persistence.createEntityManagerFactory(CatalogPersistenceUnit.NAME)
        }
    }

    @BeforeEach
    fun setup() {
        testOrder = TestOrder.getInstance()
        testCatalog = TestCatalog.getInstance()
        testUser = TestUser.getInstance()
        entityManager = emf.createEntityManager()
        catalogEntityManager = catalogEmf.createEntityManager()
        sessionContextMock = mock(SecurityContext::class.java)
        priceEngineMock = mock(PriceEngine::class.java)
        catalogItemFinder = mock(CatalogItemRepositoryImpl::class.java)
        val mailTemplateRepository = MailTemplateRepositoryImpl(entityManager)
        paymentTransactionEngine = DefaultPaymentTransactionEngine(
            OrderRepositoryImpl(entityManager, catalogEntityManager, OrderConfiguration("20", "3")),
            mailTemplateRepository,
            Mailer(),
            catalogEntityManager,
        )
        service = Orders(
            entityManager,
            OrderRepositoryImpl(entityManager, catalogEntityManager, OrderConfiguration("11.0", "20.0")),
            UserRepositoryImpl(entityManager),
            mailTemplateRepository,
            Mailer(),
            priceEngineMock,
            paymentTransactionEngine,
            catalogItemFinder,
        )
    }

    @Test
    fun find() {
        assertThat(service.find(sessionContextMock, 1L, null)).isEqualTo(testOrder.firstOrder())
    }

    @Test
    fun find_withEnhanceResult_shouldReturnEnhancedOrder() {
        val order = service.find(sessionContextMock, 1L, true)
        assertThat(order).isEqualTo(testOrder.firstOrder())
        assertThatOrderIsEnhanced(order)
    }

    @Test
    fun find_withUnknownId_ShouldThrowException() =
        assertWebException(Response.Status.NOT_FOUND) { service.find(sessionContextMock, 999L, null) }

    @Test
    fun find_whenClientHasUserRoleAndOrderBelongsToAnotherUser_ShouldThrowException() {
        val user = User("777@test.com", "test", "M.", "John", "Doe", "+33616161616", null, null, "fr_FR", null)
        entityManager.transaction.begin()
        entityManager.persist(user)
        entityManager.transaction.commit()
        `when`(sessionContextMock.isUserInRole(JeeshopRoles.USER)).thenReturn(true)
        `when`(sessionContextMock.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(false)
        `when`(sessionContextMock.userPrincipal).thenReturn(BasicUserPrincipal(user.login))
        assertWebException(Response.Status.UNAUTHORIZED) { service.find(sessionContextMock, 1L, null) }
        entityManager.transaction.begin()
        entityManager.remove(user)
        entityManager.transaction.commit()
    }

    @Test
    fun findAll_shouldReturnNoneEmptyList() {
        assertThat(service.findAll(sessionContextMock, null, null, null, null, null, null, null, null))
            .containsExactly(testOrder.firstOrder(), testOrder.secondOrder())
    }

    @Test
    fun findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        val orders = service.findAll(sessionContextMock, null, 0, 1, null, null, null, null, null)
        assertThat(orders).containsExactly(testOrder.firstOrder())
    }

    @Test
    fun findAll_ByLogin_shouldReturnSearchedOrder() {
        val orders = service.findAll(
            sessionContextMock,
            testOrder.firstOrder().customer!!.login,
            0,
            1,
            null,
            null,
            null,
            null,
            null,
        )
        assertThat(orders).containsExactly(testOrder.firstOrder())
    }

    @Test
    fun findAll_ByStatus_shouldReturnOrdersWithMatchingStatus() {
        val orders = service.findAll(sessionContextMock, null, 0, 1, null, null, OrderStatus.PAYMENT_VALIDATED, null, null)
        assertThat(orders).containsExactly(testOrder.firstOrder())
    }

    @Test
    fun findAll_ByStatus_shouldReturnEmptyListWhenThereAreNoOrdersInGivenStatus() {
        val orders = service.findAll(sessionContextMock, null, 0, 1, null, null, OrderStatus.DELIVERED, null, null)
        assertThat(orders).isEmpty()
    }

    @Test
    fun findAll_ByStatus_shouldReturnEmptyListWhenThereAreNoOrdersWithItemsMatchingGivenSKUId() {
        val orders = service.findAll(sessionContextMock, null, 0, 1, null, null, null, 2L, null)
        assertThat(orders).isEmpty()
    }

    @Test
    fun findAll_withEnhancedResults_shouldReturnEnhancedOrders() {
        val orders = service.findAll(sessionContextMock, null, 0, 1, null, null, null, null, true)
        assertThat(orders).containsExactly(testOrder.firstOrder())
        assertThat(orders[0].deliveryFee).isNotNull
        assertThatOrderIsEnhanced(orders[0])
    }

    @Test
    fun findAll_BySkuId_shouldReturnOrdersWithItemsHavingGivenSkuIdAndStatus() {
        val orders = service.findAll(
            sessionContextMock,
            null,
            0,
            1,
            null,
            null,
            OrderStatus.PAYMENT_VALIDATED,
            1L,
            null,
        )
        assertThat(orders).containsExactly(testOrder.firstOrder())
    }

    @Test
    fun findAll_BySkuId_shouldReturnOrdersWithItemsHavingGivenSkuId() {
        val orders = service.findAll(sessionContextMock, null, 0, 1, null, null, null, 1L, null)
        assertThat(orders).containsExactly(testOrder.firstOrder())
    }

    @Test
    fun findAll_whenClientHasUserRoleOnly_shouldReturnNoneEmptyList_WithoutOrdersWithStatusCREATED() {
        setUserRoleContext()
        assertThat(service.findManaged(sessionContextMock, null, null, null, null, null, null, null, null))
            .containsExactly(testOrder.firstOrder())
    }

    @Test
    fun findAll_whenClientHasUserRoleOnlyAndwithPagination_shouldReturnNoneEmptyListPaginated() {
        setUserRoleContext()
        val orders = service.findAll(sessionContextMock, null, 0, 1, null, null, null, null, null)
        assertThat(orders).containsExactly(testOrder.firstOrder())
    }

    @Test
    fun findAll_whenClientHasUserRoleOnlyAndByLogin_shouldReturnSearchedOrder() {
        setUserRoleContext()
        val orders = service.findAll(
            sessionContextMock,
            testOrder.firstOrder().customer!!.login,
            0,
            1,
            null,
            null,
            null,
            null,
            null,
        )
        assertThat(orders).containsExactly(testOrder.firstOrder())
    }

    @Test
    fun findAll_whenClientHasUserRoleOnlyAndByStatus_shouldReturnOrdersWithMatchingStatus() {
        setUserRoleContext()
        val orders = service.findAll(
            sessionContextMock,
            null,
            0,
            1,
            null,
            null,
            OrderStatus.PAYMENT_VALIDATED,
            null,
            null,
        )
        assertThat(orders).containsExactly(testOrder.firstOrder())
    }

    @Test
    fun findAll_whenClientHasUserRoleAndByStatusCREATED_shouldReturnOrdersWithThisStatus() {
        setUserRoleContext()
        val orders = service.findAll(sessionContextMock, null, 0, 1, null, null, OrderStatus.CREATED, null, null)
        assertThat(orders).containsExactly(testOrder.secondOrder())
    }

    @Test
    fun count() {
        assertThat(service.count(null, null, null)).isGreaterThan(0)
    }

    @Test
    fun count_withUnknownSearchCriteria() {
        assertThat(service.count("unknown", null, null)).isEqualTo(0)
    }

    @Test
    fun create_shouldThrowBadRequestWhenParametersHaveId() {
        val address = address().apply { id = 777L }
        val orderItem = OrderItem().apply { id = 777L }
        val orderItems = mutableSetOf(orderItem)
        assertWebException(Response.Status.BAD_REQUEST) {
            service.create(sessionContextMock, Order(null, address, address()), null)
        }
        assertWebException(Response.Status.BAD_REQUEST) {
            service.create(sessionContextMock, Order(null, address(), address), null)
        }
        assertWebException(Response.Status.BAD_REQUEST) {
            service.create(sessionContextMock, Order(orderItems, address(), address), null)
        }
    }

    @Test
    fun create_shouldPersistOrderAndItsAddressesInCascade_SetCurrentUserToOrderForUserRole_AndProcessPayment() {
        val deliveryAddress = address()
        val billingAddress = address()
        setUserRoleContext()
        entityManager.transaction.begin()
        val order = Order(hashSetOf(), address(), address()).apply {
            storeId = 1L
            orderDiscounts = hashSetOf()
        }
        service.create(sessionContextMock, order, null)
        entityManager.transaction.commit()
        verify(sessionContextMock).isUserInRole(JeeshopRoles.USER)
        val persistedOrder = entityManager.find(Order::class.java, order.id!!)
        assertThat(persistedOrder).isNotNull
        assertThat(persistedOrder.status).isEqualTo(OrderStatus.PAYMENT_VALIDATED)
        assertThat(persistedOrder.customer).isEqualTo(testOrder.firstOrdersUser())
        deliveryAddress.id = persistedOrder.deliveryAddress!!.id
        billingAddress.id = persistedOrder.billingAddress!!.id
        assertThat(persistedOrder.billingAddress).isEqualTo(billingAddress)
        assertThat(persistedOrder.deliveryAddress).isEqualTo(deliveryAddress)
        entityManager.transaction.begin()
        entityManager.remove(order)
        entityManager.transaction.commit()
    }

    @Test
    fun create_shouldPersistOrderWithOrderItems_computePrice_andProcessPayment() {
        val orderItems = hashSetOf(OrderItem(1L, 1L, 2), OrderItem(2L, 2L, 3))
        setUserRoleContext()
        entityManager.transaction.begin()
        val order = Order(orderItems, address(), address()).apply {
            orderDiscounts = hashSetOf()
            storeId = 1L
        }
        service.create(sessionContextMock, order, null)
        entityManager.transaction.commit()
        verify(sessionContextMock).isUserInRole(JeeshopRoles.USER)
        verify(priceEngineMock).computePrice(order)
        val persistedOrder = entityManager.find(Order::class.java, order.id!!)
        assertThat(persistedOrder).isNotNull
        assertThat(persistedOrder.status).isEqualTo(OrderStatus.PAYMENT_VALIDATED)
        assertThat(persistedOrder.customer).isEqualTo(testOrder.firstOrdersUser())
        assertThat(persistedOrder.items).hasSize(2)
        entityManager.transaction.begin()
        entityManager.remove(order)
        entityManager.transaction.commit()
    }

    @Test
    fun create_shouldSetGivenUserByLoginInOrder_ForADMINRole() {
        `when`(sessionContextMock.isUserInRole(JeeshopRoles.USER)).thenReturn(false)
        `when`(sessionContextMock.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(true)
        entityManager.transaction.begin()
        val order = Order(hashSetOf(), address(), address()).apply {
            storeId = 1L
            orderDiscounts = hashSetOf()
        }
        service.create(sessionContextMock, order, "test@test.com")
        entityManager.transaction.commit()
        verify(sessionContextMock).isUserInRole(JeeshopRoles.USER)
        verify(sessionContextMock).isUserInRole(JeeshopRoles.ADMIN)
        val persistedOrder = entityManager.find(Order::class.java, order.id!!)
        assertThat(persistedOrder).isNotNull
        assertThat(persistedOrder.status).isEqualTo(OrderStatus.PAYMENT_VALIDATED)
        assertThat(persistedOrder.customer).isEqualTo(testOrder.firstOrdersUser())
        entityManager.transaction.begin()
        entityManager.remove(order)
        entityManager.transaction.commit()
    }

    @Test
    fun delete_shouldRemove() {
        val order = Order().apply {
            storeId = 1L
            customer = testUser.firstUser()
            status = OrderStatus.VALIDATED
        }
        entityManager.transaction.begin()
        entityManager.persist(order)
        entityManager.transaction.commit()
        entityManager.transaction.begin()
        service.delete(order.id!!)
        entityManager.transaction.commit()
        assertThat(entityManager.find(Order::class.java, order.id!!)).isNull()
    }

    @Test
    fun delete_NotExistingEntry_shouldThrowNotFoundEx() =
        assertWebException(Response.Status.NOT_FOUND) { service.delete(666L) }

    @Test
    fun modifyUnknownCatalog_ShouldThrowNotFoundException() {
        val order = Order().apply { id = 9999L }
        assertWebException(Response.Status.NOT_FOUND) { service.modify(order) }
    }

    private fun setUserRoleContext() {
        `when`(sessionContextMock.isUserInRole(JeeshopRoles.USER)).thenReturn(true)
        `when`(sessionContextMock.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(false)
        `when`(sessionContextMock.userPrincipal).thenReturn(BasicUserPrincipal(testOrder.firstOrdersUser().login))
    }

    private fun address() = Address("7 Rue des arbres", "Paris", "92800", "John", "Doe", "M.", null, "FRA")

    private fun assertThatOrderIsEnhanced(order: Order) {
        assertThat(order.deliveryFee).isEqualTo(11.0)
        assertThat(order.vat).isEqualTo(20.0)
        order.items!!.forEach { item ->
            assertThat(item.displayName).isNotNull
            assertThat(item.skuReference).isNotNull
        }
        order.orderDiscounts!!.forEach { discount ->
            assertThat(discount.displayName).isNotNull
            assertThat(discount.rateType).isNotNull
        }
    }

    private fun assertWebException(status: Response.Status, operation: () -> Unit) {
        try {
            operation()
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(status)
            return
        }
        throw AssertionError("Expected WebApplicationException with status $status")
    }
}
