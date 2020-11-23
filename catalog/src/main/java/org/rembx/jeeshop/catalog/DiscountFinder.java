package org.rembx.jeeshop.catalog;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.quarkus.hibernate.orm.PersistenceUnit;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Discount;
import org.rembx.jeeshop.catalog.model.Discount.ApplicableTo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

import static org.rembx.jeeshop.catalog.model.QDiscount.discount;

public class DiscountFinder {

    private EntityManager entityManager;

    public DiscountFinder() {
    }

    DiscountFinder(@PersistenceUnit(CatalogPersistenceUnit.NAME) EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /*
     * Returns all discounts visible to end user
     * @param applicableTo the applicable type of discount
     * @param locale the locale for results localization
     * @return the visible discounts: not disabled, with startDate and endDate respectively before and after now and without
     * voucher code
     */
    public List<Discount> findVisibleDiscounts(ApplicableTo applicableTo, String locale) {
        Date now = new Date();
        List<Discount> results = new JPAQueryFactory(entityManager)
                .selectFrom(discount).where(
                        discount.disabled.isFalse(),
                        discount.endDate.after(now).or(discount.endDate.isNull()),
                        discount.startDate.before(now).or(discount.startDate.isNull()),
                        discount.applicableTo.eq(applicableTo),
                        discount.voucherCode.isNull()
                ).fetch();

        results.forEach((discount) -> discount.setLocalizedPresentation(locale));

        return results;

    }


    /*
     * Returns all discounts eligible for end-user
     * @param applicableTo the applicable type of discount
     * @param locale the locale for results localization
     * @return the visible discounts: not disabled, with startDate and endDate respectively before and after now and without
     * voucher code, applicable to given number of orders
     */
    public List<Discount> findEligibleOrderDiscounts(String locale, Long completedOrderNumbers) {
        Date now = new Date();
        List<Discount> results = new JPAQueryFactory(entityManager)
                .selectFrom(discount).where(
                        discount.disabled.isFalse(),
                        discount.endDate.after(now).or(discount.endDate.isNull()),
                        discount.startDate.before(now).or(discount.startDate.isNull()),
                        discount.applicableTo.eq(ApplicableTo.ORDER),
                        discount.triggerRule.ne(Discount.Trigger.ORDER_NUMBER).or(
                                discount.triggerValue.eq(completedOrderNumbers.doubleValue() + 1)
                        ),
                        discount.voucherCode.isNull()
                )
                .fetch();

        results.forEach((discount) -> discount.setLocalizedPresentation(locale));

        return results;

    }

}
