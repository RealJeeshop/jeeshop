package org.rembx.jeeshop.catalog.infrastructure.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import io.quarkus.hibernate.orm.PersistenceUnit
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import org.rembx.jeeshop.catalog.domain.DiscountRepository
import org.rembx.jeeshop.catalog.domain.Discount
import org.rembx.jeeshop.catalog.domain.Discount.ApplicableTo
import org.rembx.jeeshop.catalog.domain.QDiscount.discount
import java.util.Date

@ApplicationScoped
class DiscountRepositoryImpl(
    @PersistenceUnit(CatalogPersistenceUnit.NAME)
    private val entityManager: EntityManager
) : DiscountRepository {
    override fun findVisibleDiscounts(applicableTo: ApplicableTo, locale: String?): List<Discount> {
        val now = Date()
        val results = JPAQueryFactory(entityManager).selectFrom(discount).where(
            discount.disabled.isFalse,
            discount.endDate.after(now).or(discount.endDate.isNull),
            discount.startDate.before(now).or(discount.startDate.isNull),
            discount.applicableTo.eq(applicableTo),
            discount.voucherCode.isNull
        ).fetch()
        results.forEach { it.setLocalizedPresentation(locale) }
        return results
    }

    override fun findEligibleOrderDiscounts(locale: String?, completedOrderNumbers: Long): List<Discount> {
        val now = Date()
        val results = JPAQueryFactory(entityManager).selectFrom(discount).where(
            discount.disabled.isFalse,
            discount.endDate.after(now).or(discount.endDate.isNull),
            discount.startDate.before(now).or(discount.startDate.isNull),
            discount.applicableTo.eq(ApplicableTo.ORDER),
            discount.triggerRule.ne(Discount.Trigger.ORDER_NUMBER)
                .or(discount.triggerValue.eq(completedOrderNumbers.toDouble() + 1)),
            discount.voucherCode.isNull
        ).fetch()
        results.forEach { it.setLocalizedPresentation(locale) }
        return results
    }
}
