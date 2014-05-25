package org.rembx.jeeshop.catalog.util;

import org.rembx.jeeshop.catalog.model.Category;
import org.rembx.jeeshop.catalog.model.Product;

import java.util.List;

/**
 * Created by remi on 25/05/14.
 */
public class Assertions extends org.fest.assertions.Assertions {

    public static TestCatalog.CategoriesAssert assertThatCategoriesOf(List<Category> categories) {
        return new TestCatalog.CategoriesAssert(categories);
    }

    public static TestCatalog.ProductsAssert assertThatProductsOf(List<Product> products) {
        return new TestCatalog.ProductsAssert(products);
    }

}
