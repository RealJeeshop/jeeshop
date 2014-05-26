package org.rembx.jeeshop.catalog.util;

import org.fest.assertions.GenericAssert;
import org.rembx.jeeshop.catalog.model.Catalog;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Category;
import org.rembx.jeeshop.catalog.model.Product;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Catalog test utility with DSL
 */
public class TestCatalog {

    private static TestCatalog instance;

    // Date are initialized with java.sql.Timestamp as JPA get a Timestamp instance
    private final static Date now = Timestamp.from(ZonedDateTime.now().toInstant());
    private final static Date Tomorrow = Timestamp.from(ZonedDateTime.now().plusDays(1).toInstant());
    private final static Date yesterday = Timestamp.from(ZonedDateTime.now().minusDays(1).toInstant());


    private static Catalog catalog;
    public static Catalog emptyCatalog;

    public static TestCatalog getInstance() {
        EntityManager entityManager = Persistence.createEntityManagerFactory(CatalogPersistenceUnit.NAME).createEntityManager();

        if (instance !=null)
            return instance;

        entityManager.getTransaction().begin();

        emptyCatalog = new Catalog("empty");
        entityManager.persist(emptyCatalog);

        catalog = new Catalog("test");

        Category rootCat1Empty = new Category("rootCat1", "Root category 1 empty", now, Tomorrow, false);
        Category rootCat2 = new Category("rootCat2", "Root category 2 with child categories", now, Tomorrow, false);
        Category rootCat3Expired = new Category("rootCat3", "Root category 3 expired", now, yesterday, false);


        Category childCat1Empty = new Category("childCat1", "Child category 1", now, Tomorrow, false);
        Category childCat2 = new Category("childCat2", "Child category 2 with products", now, Tomorrow, false);
        Category childCat3Expired = new Category("childCat3", "Child category 3 expired", now, yesterday, false);
        Category childCat4Disabled = new Category("childCat4", "Child category 4 disabled", now, Tomorrow, true);

        Product product1 = new Product("product1", now, Tomorrow, false);
        Product product2Expired = new Product("product2", now, yesterday, false);
        Product product3Disabled = new Product("product3", now, yesterday, true);

        catalog.setRootCategories(Arrays.asList(rootCat1Empty, rootCat2, rootCat3Expired));
        rootCat2.setChildCategories(Arrays.asList(childCat1Empty, childCat2, childCat3Expired, childCat4Disabled));
        childCat2.setChildProducts(Arrays.asList(product1, product2Expired, product3Disabled));

        entityManager.persist(catalog);
        entityManager.getTransaction().commit();

        instance = new TestCatalog();
        entityManager.close();
        return instance;
    }


    public Long getId(){
        return catalog.getId();
    }

    public Long getEmptyCatalogId() {return emptyCatalog.getId();}

    public Category aRootCategoryWithChildCategories(){
        return catalog.getRootCategories().get(1);
    }

    public Category aCategoryWithProducts(){
        return aRootCategoryWithChildCategories().getChildCategories().get(1);
    }

    public Category aCategoryWithoutProducts(){
        return aRootCategoryWithChildCategories().getChildCategories().get(0);
    }

    public Category anExpiredCategory(){
        return aRootCategoryWithChildCategories().getChildCategories().get(2);
    }

    public Category aDisableCategory(){
        return aRootCategoryWithChildCategories().getChildCategories().get(3);
    }

    public static class CategoriesAssert extends GenericAssert<CategoriesAssert, List<Category>> {

        CategoriesAssert( List<Category> actual) {
            super(CategoriesAssert.class , actual);
        }

        /**
         * Visible categories are not disabled and have an endDate after current date
         */
        public CategoriesAssert areVisibleRootCategories(){
            assertThat(actual).containsExactly(catalog.getRootCategories().get(0), catalog.getRootCategories().get(1));
            return this;
        }

        /**
         * Visible categories are not disabled and have an endDate after current date
         */
        public CategoriesAssert areVisibleChildCategoriesOfARootCategoryWithChildCategories(){
            assertThat(actual).containsExactly(catalog.getRootCategories().get(1).getChildCategories().get(0),
                    catalog.getRootCategories().get(1).getChildCategories().get(1));
            return this;
        }

    }

    public static class ProductsAssert extends GenericAssert<ProductsAssert, List<Product>> {

        ProductsAssert( List<Product> actual) {
            super(ProductsAssert.class , actual);
        }

        /**
         * Visible categories are not disabled and have an endDate after current date
         */
        public ProductsAssert areVisibleProductsOfAChildCategoryWithProducts(){
            assertThat(actual).containsExactly(catalog.getRootCategories().get(1).getChildCategories().get(1).getChildProducts().get(0));
            return this;
        }

    }

}
