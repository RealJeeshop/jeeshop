package org.rembx.jeeshop.catalog.test

import org.rembx.jeeshop.catalog.domain.CatalogItem
import org.rembx.jeeshop.catalog.domain.Category
import org.rembx.jeeshop.catalog.domain.Discount
import org.rembx.jeeshop.catalog.domain.Product
import org.rembx.jeeshop.catalog.domain.SKU

fun assertThat(catalogItem: CatalogItem): TestCatalog.CatalogItemAssert = TestCatalog.CatalogItemAssert(catalogItem)
fun assertThatCategoriesOf(categories: List<Category>): TestCatalog.CategoriesAssert = TestCatalog.CategoriesAssert(categories)
fun assertThatProductsOf(products: List<Product>): TestCatalog.ProductsAssert = TestCatalog.ProductsAssert(products)
fun assertThatSKUsOf(skus: List<SKU>): TestCatalog.SKUsAssert = TestCatalog.SKUsAssert(skus)
fun assertThatDiscountsOf(discounts: List<Discount>): TestCatalog.SKUDiscountsAssert = TestCatalog.SKUDiscountsAssert(discounts)
