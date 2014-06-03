package org.rembx.jeeshop.user.test;

import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Catalog test utility with DSL
 */
public class TestUser {

    private static TestUser instance;

    // Date are initialized with java.sql.Timestamp as JPA get a Timestamp instance
    private final static Date now = Timestamp.from(ZonedDateTime.now().toInstant());
    private final static Date yesterday = Timestamp.from(ZonedDateTime.now().minusDays(1).toInstant());

    public static TestUser getInstance() {
       if (instance !=null)
            return instance;

        EntityManager entityManager = Persistence.createEntityManagerFactory(UserPersistenceUnit.NAME).createEntityManager();

        entityManager.getTransaction().begin();

        entityManager.getTransaction().commit();

        instance = new TestUser();
        entityManager.close();
        return instance;
    }



    /*public static class SKUDiscountsAssert extends GenericAssert<SKUDiscountsAssert, List<Discount>> {

        SKUDiscountsAssert( List<Discount> actual) {
            super(SKUDiscountsAssert.class , actual);
        }

        *//**
         * Visible discounts are not disabled and have an endDate after current date
         *//*
        public SKUDiscountsAssert areVisibleDiscountsOfASKUWithDiscounts(){
            assertThat(actual).containsExactly(catalog.getRootCategories().get(1).getChildCategories().get(1)
                    .getChildProducts().get(0).getChildSKUs().get(4).getDiscounts().get(0));
            return this;
        }

    }*/

}
