package org.rembx.jeeshop.catalog.test

import com.google.common.collect.Lists
import jakarta.persistence.EntityManager
import jakarta.persistence.Persistence
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions.assertThat
import org.rembx.jeeshop.catalog.domain.Catalog
import org.rembx.jeeshop.catalog.domain.CatalogItem
import org.rembx.jeeshop.catalog.infrastructure.repository.CatalogPersistenceUnit
import org.rembx.jeeshop.catalog.domain.Category
import org.rembx.jeeshop.catalog.domain.Discount
import org.rembx.jeeshop.catalog.domain.Premises
import org.rembx.jeeshop.catalog.domain.PremisesAddress
import org.rembx.jeeshop.catalog.domain.PremisesOpeningSchedules
import org.rembx.jeeshop.catalog.domain.Presentation
import org.rembx.jeeshop.catalog.domain.Product
import org.rembx.jeeshop.catalog.domain.SKU
import org.rembx.jeeshop.catalog.domain.Store
import java.sql.Timestamp
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.Arrays
import java.util.Date
import java.util.HashMap
import java.util.Locale

class TestCatalog {
    companion object {
        private var instance: TestCatalog? = null

        private val now: Date = toDatabaseTimestamp(ZonedDateTime.now())
        private val tomorrow: Date = toDatabaseTimestamp(ZonedDateTime.now().plusDays(1))
        private val yesterday: Date = toDatabaseTimestamp(ZonedDateTime.now().minusDays(1))

        const val OWNER = "777@test.com"

        private lateinit var store: Store
        private lateinit var hiddenStore: Store
        private lateinit var premises: Premises
        private lateinit var premisesAddress: PremisesAddress
        private lateinit var premisesOpeningSchedules: PremisesOpeningSchedules

        private lateinit var catalog: Catalog
        private lateinit var emptyCatalog: Catalog

        private lateinit var rootCat1Empty: Category
        private lateinit var rootCat2: Category
        private lateinit var rootCat3Expired: Category
        private lateinit var childCat1Empty: Category
        private lateinit var childCat2: Category
        private lateinit var childCat3Expired: Category
        private lateinit var childCat4Disabled: Category
        private lateinit var childCat5WithPresentation: Category
        private lateinit var childCat6WithoutPresentation: Category
        private lateinit var product1: Product
        private lateinit var product2Expired: Product
        private lateinit var product3Disabled: Product
        private lateinit var product4: Product

        private lateinit var sku1: SKU
        private lateinit var sku2: SKU
        private lateinit var sku3: SKU
        private lateinit var sku4: SKU
        private lateinit var sku5: SKU

        private lateinit var discount1: Discount

        private fun toDatabaseTimestamp(dateTime: ZonedDateTime): Timestamp {
            val timestamp = Timestamp.from(dateTime.toInstant())
            timestamp.nanos = timestamp.nanos / 1_000 * 1_000
            return timestamp
        }

        @JvmStatic
        fun getInstance(): TestCatalog {
            if (instance != null) {
                return instance!!
            }

            val entityManager: EntityManager = Persistence.createEntityManagerFactory(CatalogPersistenceUnit.NAME).createEntityManager()
            entityManager.transaction.begin()

            emptyCatalog = Catalog("empty")
            emptyCatalog.owner = OWNER
            entityManager.persist(emptyCatalog)

            catalog = Catalog("test")
            catalog.owner = OWNER

            rootCat1Empty = Category("rootCat1", "Root category 1 empty", now, tomorrow, false, OWNER)
            rootCat2 = Category("rootCat2", "Root category 2 with child categories", now, tomorrow, false, OWNER)
            rootCat3Expired = Category("rootCat3", "Root category 3 expired", now, yesterday, false, OWNER)

            childCat1Empty = Category("childCat1", "Child category 1", now, tomorrow, false, OWNER)
            childCat2 = Category("childCat2", "Child category 2 with products", now, tomorrow, false, OWNER)
            childCat3Expired = Category("childCat3", "Child category 3 expired", now, yesterday, false, OWNER)
            childCat4Disabled = Category("childCat4", "Child category 4 disabled", now, tomorrow, true, OWNER)
            childCat5WithPresentation = Category("childCat5", "Child category 5 with presentation", now, tomorrow, false, OWNER)
            childCat6WithoutPresentation = Category("childCat6", "Child category 6 without presentation", now, tomorrow, true, OWNER)
            childCat6WithoutPresentation.presentationByLocale = HashMap()
            val presentationUKChildCat5 = Presentation("en", "Chocolat cakes", PresentationTexts.TEXT_1000, PresentationTexts.TEXT_2000)
            val presentationUSChildCat5 = Presentation("en_US", "Chocolat cakes", PresentationTexts.TEXT_1000, PresentationTexts.TEXT_2000)
            val map = hashMapOf<String, Presentation>()
            map[Locale.ENGLISH.toString()] = presentationUKChildCat5
            map[Locale.US.toString()] = presentationUSChildCat5
            childCat5WithPresentation.presentationByLocale = map

            product1 = Product("product1", "description", now, tomorrow, false, OWNER)
            product2Expired = Product("product2", "description", now, yesterday, false, OWNER)
            product3Disabled = Product("product3", "description", now, yesterday, true, OWNER)
            product4 = Product("product4", "description", now, yesterday, false, OWNER)

            sku1 = SKU("sku1", "Sku1 enabled", 10.0, 100, "X1213JJLB-1", now, tomorrow, false, 3, OWNER)
            sku2 = SKU("sku2", "Sku2 disabled", 10.0, 100, "X1213JJLB-2", now, tomorrow, true, 3, OWNER)
            sku3 = SKU("sku3", "Sku3 expired", 10.0, 100, "X1213JJLB-3", now, yesterday, false, 3, OWNER)
            sku4 = SKU("sku4", "Sku4 not available", 10.0, 2, "X1213JJLB-4", now, tomorrow, false, 3, OWNER)
            sku5 = SKU("sku5", "Sku5 with discounts", 10.0, 100, "X1213JJLB-5", now, tomorrow, false, 3, OWNER)

            discount1 = Discount("discount1", "a discount", Discount.ApplicableTo.ORDER, Discount.Type.DISCOUNT_RATE, Discount.Trigger.AMOUNT, null, 0.1, 2.0, 1, true, now, tomorrow, false, OWNER)
            sku5.discounts = Arrays.asList(discount1)

            catalog.rootCategories = Arrays.asList(rootCat1Empty, rootCat2, rootCat3Expired)
            rootCat2.childCategories = Arrays.asList(childCat1Empty, childCat2, childCat3Expired, childCat4Disabled, childCat5WithPresentation, childCat6WithoutPresentation)
            childCat2.childProducts = Arrays.asList(product1, product2Expired, product3Disabled, product4)
            product1.childSKUs = Arrays.asList(sku1, sku2, sku3, sku4, sku5)

            hiddenStore = Store("Hidden Shop")
            hiddenStore.owner = OWNER
            hiddenStore.disabled = true
            entityManager.persist(hiddenStore)

            store = Store("Shop")
            store.owner = OWNER

            premisesOpeningSchedules = PremisesOpeningSchedules(store, DayOfWeek.MONDAY, LocalTime.MIN, LocalTime.MAX)
            premisesAddress = PremisesAddress("10, rue des lilas", "Paris", "75001", "FRA")

            premises = Premises()
            premises.address = premisesAddress
            premises.schedules = Lists.newArrayList(premisesOpeningSchedules)

            store.premisses = Lists.newArrayList(premises)
            store.catalogs = Lists.newArrayList(catalog)

            entityManager.persist(store)
            entityManager.transaction.commit()

            instance = TestCatalog()
            entityManager.close()
            return instance!!
        }

        @JvmStatic
        fun getCatalog(): Catalog = catalog
    }

    fun getCatalogId(): Long? = catalog.id

    fun getEmptyCatalogId(): Long? = emptyCatalog.id

    fun aRootCategoryWithChildCategories(): Category = rootCat2
    fun aCategoryWithProducts(): Category = childCat2
    fun aCategoryWithoutProducts(): Category = childCat1Empty
    fun aCategoryWithPresentation(): Category = childCat5WithPresentation
    fun aCategoryWithoutPresentation(): Category = childCat6WithoutPresentation
    fun anExpiredCategory(): Category = childCat3Expired
    fun aDisabledCategory(): Category = childCat4Disabled
    fun anExpiredProduct(): Product = product2Expired
    fun aDisabledProduct(): Product = product3Disabled
    fun aProductWithSKUs(): Product = product1
    fun aProductWithoutSKUs(): Product = product4
    fun aVisibleSKU(): SKU = sku1
    fun aDisabledSKU(): SKU = sku2
    fun anExpiredSKU(): SKU = sku3
    fun aSKUNotAvailable(): SKU = sku4
    fun aSKUWithDiscounts(): SKU = sku5
    fun aVisibleDisount(): Discount = discount1

    class CatalogItemAssert(actual: CatalogItem) : AbstractAssert<CatalogItemAssert, CatalogItem>(actual, CatalogItemAssert::class.java) {
        fun hasLocalizedPresentationShortDescription(locale: String, text: String): CatalogItemAssert {
            val presentation = actual.presentationByLocale?.get(locale) ?: actual.presentationByLocale?.values?.firstOrNull()
            assertThat(presentation?.shortDescription).isEqualTo(text)
            if (actual.localizedPresentation != null) {
                assertThat(actual.localizedPresentation?.shortDescription).isEqualTo(text)
            }
            return this
        }
    }

    class CategoriesAssert(actual: List<Category>) : AbstractAssert<CategoriesAssert, List<Category>>(actual, CategoriesAssert::class.java) {
        fun areVisibleRootCategories(): CategoriesAssert {
            assertThat(actual).containsExactly(catalog.rootCategories!![0], catalog.rootCategories!![1])
            return this
        }

        fun areVisibleChildCategoriesOfARootCategoryWithChildCategories(): CategoriesAssert {
            val childCategories = catalog.rootCategories!![1].childCategories!!
            assertThat(actual).containsExactly(
                childCategories[0],
                childCategories[1],
                childCategories[4]
            )
            return this
        }
    }

    class ProductsAssert(actual: List<Product>) : AbstractAssert<ProductsAssert, List<Product>>(actual, ProductsAssert::class.java) {
        fun areVisibleProductsOfAChildCategoryWithProducts(): ProductsAssert {
            val products = catalog.rootCategories!![1].childCategories!![1].childProducts!!
            assertThat(actual).containsExactly(products[0])
            return this
        }
    }

    class SKUsAssert(actual: List<SKU>) : AbstractAssert<SKUsAssert, List<SKU>>(actual, SKUsAssert::class.java) {
        fun areVisibleSKUsOfAProductWithSKUs(): SKUsAssert {
            val skus = catalog.rootCategories!![1].childCategories!![1].childProducts!![0].childSKUs!!
            assertThat(actual).containsExactly(
                skus[0],
                skus[3],
                skus[4]
            )
            return this
        }
    }

    class SKUDiscountsAssert(actual: List<Discount>) : AbstractAssert<SKUDiscountsAssert, List<Discount>>(actual, SKUDiscountsAssert::class.java) {
        fun areVisibleDiscountsOfASKUWithDiscounts(): SKUDiscountsAssert {
            val discounts = catalog.rootCategories!![1].childCategories!![1].childProducts!![0].childSKUs!![4].discounts!!
            assertThat(actual).containsExactly(discounts[0])
            return this
        }
    }
}
