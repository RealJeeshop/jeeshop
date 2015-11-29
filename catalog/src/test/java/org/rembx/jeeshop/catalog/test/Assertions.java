package org.rembx.jeeshop.catalog.test;

import org.rembx.jeeshop.catalog.model.*;

import java.util.List;

/**
 * Created by remi on 25/05/14.
 */
public class Assertions extends org.assertj.core.api.Assertions{

    public static TestCatalog.CatalogItemAssert assertThat(CatalogItem catalogItem) {
        return new TestCatalog.CatalogItemAssert(catalogItem);
    }

    public static TestCatalog.CategoriesAssert assertThatCategoriesOf(List<Category> categories) {
        return new TestCatalog.CategoriesAssert(categories);
    }

    public static TestCatalog.ProductsAssert assertThatProductsOf(List<Product> products) {
        return new TestCatalog.ProductsAssert(products);
    }

    public static TestCatalog.SKUsAssert assertThatSKUsOf(List<SKU> skus) {
        return new TestCatalog.SKUsAssert(skus);
    }


    public static TestCatalog.SKUDiscountsAssert assertThatDiscountsOf(List<Discount> discounts) {
        return new TestCatalog.SKUDiscountsAssert(discounts);
    }



}
