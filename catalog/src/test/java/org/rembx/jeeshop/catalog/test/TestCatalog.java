package org.rembx.jeeshop.catalog.test;

import com.google.common.collect.Lists;
import org.assertj.core.api.AbstractAssert;
import org.rembx.jeeshop.catalog.model.*;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rembx.jeeshop.catalog.model.Discount.ApplicableTo.ORDER;
import static org.rembx.jeeshop.catalog.model.Discount.Trigger.AMOUNT;
import static org.rembx.jeeshop.catalog.model.Discount.Type.DISCOUNT_RATE;

/**
 * Catalog test utility
 */
public class TestCatalog {

    private static TestCatalog instance;

    // Date are initialized with java.sql.Timestamp as JPA get a Timestamp instance
    private final static Date now = Timestamp.from(ZonedDateTime.now().toInstant());
    private final static Date tomorrow = Timestamp.from(ZonedDateTime.now().plusDays(1).toInstant());
    private final static Date yesterday = Timestamp.from(ZonedDateTime.now().minusDays(1).toInstant());

    public final static String OWNER = "777@test.com";

    private static Store store;
    private static Store hiddenStore;

    private static Catalog catalog;
    private static Catalog emptyCatalog;

    private static Category rootCat1Empty;
    private static Category rootCat2;
    private static Category rootCat3Expired;
    private static Category childCat1Empty;
    private static Category childCat2;
    private static Category childCat3Expired;
    private static Category childCat4Disabled;
    private static Category childCat5WithPresentation;
    private static Category childCat6WithoutPresentation;
    private static Product product1;
    private static Product product2Expired;
    private static Product product3Disabled;
    private static Product product4;

    private static SKU sku1;
    private static SKU sku2;
    private static SKU sku3;
    private static SKU sku4;
    private static SKU sku5;

    private static Discount discount1;


    public static TestCatalog getInstance() {
        if (instance != null)
            return instance;

        EntityManager entityManager = Persistence.createEntityManagerFactory(CatalogPersistenceUnit.NAME).createEntityManager();

        entityManager.getTransaction().begin();

        emptyCatalog = new Catalog("empty");
        emptyCatalog.setOwner(OWNER);
        entityManager.persist(emptyCatalog);

        catalog = new Catalog("test");
        catalog.setOwner(OWNER);

        rootCat1Empty = new Category("rootCat1", "Root category 1 empty", now, tomorrow, false, OWNER);
        rootCat2 = new Category("rootCat2", "Root category 2 with child categories", now, tomorrow, false, OWNER);
        rootCat3Expired = new Category("rootCat3", "Root category 3 expired", now, yesterday, false, OWNER);


        childCat1Empty = new Category("childCat1", "Child category 1", now, tomorrow, false, OWNER);
        childCat2 = new Category("childCat2", "Child category 2 with products", now, tomorrow, false, OWNER);
        childCat3Expired = new Category("childCat3", "Child category 3 expired", now, yesterday, false, OWNER);
        childCat4Disabled = new Category("childCat4", "Child category 4 disabled", now, tomorrow, true, OWNER);
        childCat5WithPresentation = new Category("childCat5", "Child category 5 with presentation", now, tomorrow, false, OWNER);
        childCat6WithoutPresentation = new Category("childCat6", "Child category 6 without presentation", now, tomorrow, true, OWNER);
        childCat6WithoutPresentation.setPresentationByLocale(new HashMap<>());
        Presentation presentationUKChildCat5 = new Presentation("en", "Chocolat cakes", PresentationTexts.TEXT_1000, PresentationTexts.TEXT_2000);
        Presentation presentationUSChildCat5 = new Presentation("en_US", "Chocolat cakes", PresentationTexts.TEXT_1000, PresentationTexts.TEXT_2000);
        childCat5WithPresentation.setPresentationByLocale(new HashMap<String, Presentation>() {{
            put(Locale.ENGLISH.toString(), presentationUKChildCat5);
            put(Locale.US.toString(), presentationUSChildCat5);
        }});

        product1 = new Product("product1", "description", now, tomorrow, false, OWNER);
        product2Expired = new Product("product2", "description", now, yesterday, false, OWNER);
        product3Disabled = new Product("product3", "description", now, yesterday, true, OWNER);
        product4 = new Product("product4", "description", now, yesterday, false, OWNER);

        sku1 = new SKU("sku1", "Sku1 enabled", 10d, 100, "X1213JJLB-1", now, tomorrow, false, 3, OWNER);
        sku2 = new SKU("sku2", "Sku2 disabled", 10d, 100, "X1213JJLB-2", now, tomorrow, true, 3, OWNER);
        sku3 = new SKU("sku3", "Sku3 expired", 10d, 100, "X1213JJLB-3", now, yesterday, false, 3, OWNER);
        sku4 = new SKU("sku4", "Sku4 not available", 10d, 2, "X1213JJLB-4", now, tomorrow, false, 3, OWNER);
        sku5 = new SKU("sku5", "Sku5 with discounts", 10d, 100, "X1213JJLB-5", now, tomorrow, false, 3, OWNER);

        discount1 = new Discount("discount1", "a discount", ORDER, DISCOUNT_RATE, AMOUNT, null, 0.1, 2.0, 1, true, now, tomorrow, false, OWNER);
        sku5.setDiscounts(Arrays.asList(discount1));

        catalog.setRootCategories(Arrays.asList(rootCat1Empty, rootCat2, rootCat3Expired));
        rootCat2.setChildCategories(Arrays.asList(childCat1Empty, childCat2, childCat3Expired, childCat4Disabled, childCat5WithPresentation, childCat6WithoutPresentation));
        childCat2.setChildProducts(Arrays.asList(product1, product2Expired, product3Disabled, product4));
        product1.setChildSKUs(Arrays.asList(sku1, sku2, sku3, sku4, sku5));

        hiddenStore = new Store("Hidden Shop");
        hiddenStore.setOwner(OWNER);
        hiddenStore.setDisabled(true);
        entityManager.persist(hiddenStore);

        store = new Store("Shop");
        store.setOwner(OWNER);

        PremisesOpeningSchedules schedules = new PremisesOpeningSchedules(store, DayOfWeek.MONDAY, LocalTime.MIN, LocalTime.MAX);
        PremisesAddress address = new PremisesAddress("10, rue des lilas", "Paris", "75001", "FRA");

        Premises premises = new Premises();
        premises.setAddress(address);
        premises.setSchedules(Lists.newArrayList(schedules));

        store.setPremisses(Lists.newArrayList(premises));
        store.setCatalogs(Lists.newArrayList(catalog));

        entityManager.persist(store);
        entityManager.getTransaction().commit();

        instance = new TestCatalog();
        entityManager.close();
        return instance;
    }


    public Long getCatalogId() {
        return catalog.getId();
    }

    public static Catalog getCatalog() {
        return catalog;
    }

    public Long getEmptyCatalogId() {
        return emptyCatalog.getId();
    }

    public Category aRootCategoryWithChildCategories() {
        return rootCat2;
    }

    public Category aCategoryWithProducts() {
        return childCat2;
    }

    public Category aCategoryWithoutProducts() {
        return childCat1Empty;
    }

    public Category aCategoryWithPresentation() {
        return childCat5WithPresentation;
    }

    public Category aCategoryWithoutPresentation() {
        return childCat6WithoutPresentation;
    }

    public Category anExpiredCategory() {
        return childCat3Expired;
    }

    public Category aDisabledCategory() {
        return childCat4Disabled;
    }

    public Product anExpiredProduct() {
        return product2Expired;
    }

    public Product aDisabledProduct() {
        return product3Disabled;
    }

    public Product aProductWithSKUs() {
        return product1;
    }

    public Product aProductWithoutSKUs() {
        return product4;
    }

    public SKU aVisibleSKU() {
        return sku1;
    }

    public SKU aDisabledSKU() {
        return sku2;
    }

    public SKU anExpiredSKU() {
        return sku3;
    }

    public SKU aSKUNotAvailable() {
        return sku4;
    }

    public SKU aSKUWithDiscounts() {
        return sku5;
    }

    public Discount aVisibleDisount() {
        return discount1;
    }

    public static class CatalogItemAssert extends AbstractAssert<CatalogItemAssert, CatalogItem> {
        CatalogItemAssert(CatalogItem actual) {
            super(actual, CatalogItemAssert.class);
        }

        public CatalogItemAssert hasLocalizedPresentationShortDescription(String locale, String text) {
            assertThat(actual.getPresentationByLocale().get(locale).getShortDescription()).isEqualTo(text);
            assertThat(actual.getLocalizedPresentation().getShortDescription()).isEqualTo(text);
            return this;
        }
    }

    public static class CategoriesAssert extends AbstractAssert<CategoriesAssert, List<Category>> {

        CategoriesAssert(List<Category> actual) {
            super(actual, CategoriesAssert.class);
        }

        /**
         * Visible categories are not disabled and have an endDate after current date
         */
        public CategoriesAssert areVisibleRootCategories() {
            assertThat(actual).containsExactly(catalog.getRootCategories().get(0), catalog.getRootCategories().get(1));
            return this;
        }

        /**
         * Visible categories are not disabled and have an endDate after current date
         */
        public CategoriesAssert areVisibleChildCategoriesOfARootCategoryWithChildCategories() {
            assertThat(actual).containsExactly(catalog.getRootCategories().get(1).getChildCategories().get(0),
                    catalog.getRootCategories().get(1).getChildCategories().get(1), catalog.getRootCategories().get(1).getChildCategories().get(4));
            return this;
        }

    }

    public static class ProductsAssert extends AbstractAssert<ProductsAssert, List<Product>> {

        ProductsAssert(List<Product> actual) {
            super(actual, ProductsAssert.class);
        }

        /**
         * Visible products are not disabled and have an endDate after current date
         */
        public ProductsAssert areVisibleProductsOfAChildCategoryWithProducts() {
            assertThat(actual).containsExactly(catalog.getRootCategories().get(1).getChildCategories().get(1).getChildProducts().get(0));
            return this;
        }

    }

    public static class SKUsAssert extends AbstractAssert<SKUsAssert, List<SKU>> {

        SKUsAssert(List<SKU> actual) {
            super(actual, SKUsAssert.class);
        }

        /**
         * Visible skus are not disabled and have an endDate after current date
         */
        public SKUsAssert areVisibleSKUsOfAProductWithSKUs() {
            assertThat(actual).containsExactly(
                    catalog.getRootCategories().get(1).getChildCategories().get(1).getChildProducts().get(0).getChildSKUs().get(0),
                    catalog.getRootCategories().get(1).getChildCategories().get(1).getChildProducts().get(0).getChildSKUs().get(3),
                    catalog.getRootCategories().get(1).getChildCategories().get(1).getChildProducts().get(0).getChildSKUs().get(4));
            return this;
        }

    }

    public static class SKUDiscountsAssert extends AbstractAssert<SKUDiscountsAssert, List<Discount>> {

        SKUDiscountsAssert(List<Discount> actual) {
            super(actual, SKUDiscountsAssert.class);
        }

        /**
         * Visible discounts are not disabled and have an endDate after current date
         */
        public SKUDiscountsAssert areVisibleDiscountsOfASKUWithDiscounts() {
            assertThat(actual).containsExactly(catalog.getRootCategories().get(1).getChildCategories().get(1)
                    .getChildProducts().get(0).getChildSKUs().get(4).getDiscounts().get(0));
            return this;
        }

    }

}
