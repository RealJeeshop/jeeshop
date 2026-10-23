package org.rembx.jeeshop.catalog.infrastructure.api

import jakarta.ws.rs.core.Response
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.rembx.jeeshop.catalog.infrastructure.repository.CatalogItemRepositoryImpl
import org.rembx.jeeshop.catalog.domain.Category
import org.rembx.jeeshop.catalog.test.CatalogItemCRUDTester
import org.rembx.jeeshop.catalog.test.PresentationTexts
import org.rembx.jeeshop.catalog.test.TestCatalog
import org.rembx.jeeshop.catalog.test.assertThat
import org.rembx.jeeshop.catalog.test.assertThatCategoriesOf
import org.rembx.jeeshop.catalog.test.assertThatProductsOf
import org.rembx.jeeshop.rest.WebApplicationException
import java.util.Date
import java.util.Locale

class CategoriesCT {
    private lateinit var localService: Categories
    private lateinit var tester: CatalogItemCRUDTester<Category>

    @BeforeEach
    fun setup() {
        tester = CatalogItemCRUDTester(Category::class.java)
        localService = Categories(tester.getEntityManager(), CatalogItemRepositoryImpl(tester.getEntityManager()), null)
        tester.setService(localService)
    }

    @Test
    fun find_withIdOfVisibleCategory_ShouldReturnExpectedCategory() {
        tester.setAdminUser()
        val category = localService.find(tester.getSecurityContext(), tester.getFixtures().aCategoryWithProducts().id, null)
        assertThat(category).isEqualTo(tester.getFixtures().aCategoryWithProducts())
        assertThat(category.isVisible()).isTrue
    }

    @Test
    fun find_withIdOfDisableCategory_ShouldThrowForbiddenException() {
        try {
            tester.setPublicUser()
            localService.find(tester.getSecurityContext(), tester.getFixtures().aDisabledCategory().id, null)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.FORBIDDEN)
        }
    }

    @Test
    fun find_withIdOfExpiredCategory_ShouldThrowForbiddenException() {
        try {
            tester.setPublicUser()
            localService.find(tester.getSecurityContext(), tester.getFixtures().anExpiredCategory().id, null)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.FORBIDDEN)
        }
    }

    @Test
    fun find_idOfCategoryWithPresentation_ShouldReturnExpectedPresentation() {
        tester.setPublicUser()
        val actual = localService.find(tester.getSecurityContext(), tester.getFixtures().aCategoryWithPresentation().id, Locale.ENGLISH.toString())
        TestCatalog.CatalogItemAssert(actual).hasLocalizedPresentationShortDescription(Locale.ENGLISH.toString(), PresentationTexts.TEXT_1000)
    }

    @Test
    fun find_idOfCategoryWithPresentation_WithNoLocaleSpecifiedShouldReturnFallbackLocalePresentation() {
        tester.setPublicUser()
        val category = localService.find(tester.getSecurityContext(), tester.getFixtures().aCategoryWithPresentation().id, null)
        TestCatalog.CatalogItemAssert(category).hasLocalizedPresentationShortDescription(Locale.ENGLISH.toString(), PresentationTexts.TEXT_1000)
    }

    @Test
    fun find_idOfCategoryWithPresentation_WithNotSupportedLocaleSpecifiedShouldReturnFallbackLocalePresentation() {
        tester.setPublicUser()
        val category = localService.find(tester.getSecurityContext(), tester.getFixtures().aCategoryWithPresentation().id, "it_IT")
        TestCatalog.CatalogItemAssert(category).hasLocalizedPresentationShortDescription(Locale.ENGLISH.toString(), PresentationTexts.TEXT_1000)
    }

    @Test
    fun findLocales_OfACategoryWithPresentations_shouldReturnExpectedPresentations() {
        tester.setAdminUser()
        val locales = localService.findPresentationsLocales(tester.getSecurityContext(), tester.getFixtures().aCategoryWithPresentation().id)

        assertThat(locales).containsOnly(Locale.US.toString(), Locale.ENGLISH.toString())
    }

    @Test
    fun find_withUnknownCategoryId_ShouldThrowNotFoundException() {
        try {
            tester.setAdminUser()
            localService.find(tester.getSecurityContext(), 9999L, null)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
        }
    }

    @Test
    fun findCategories_shouldReturn404ExWhenCategoryNotFound() {
        try {
            tester.setAdminUser()
            localService.findChildCategories(tester.getSecurityContext(), 9999L, null)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
        }
    }

    @Test
    fun findCategories_shouldNotReturnExpiredNorDisabledCategories() {
        tester.setPublicUser()
        val categories = localService.findChildCategories(tester.getSecurityContext(), tester.getFixtures().aRootCategoryWithChildCategories().id, null)
        assertThat(categories).isNotEmpty
        assertThatCategoriesOf(categories).areVisibleChildCategoriesOfARootCategoryWithChildCategories()
    }

    @Test
    fun findCategories_shouldReturnEmptyListWhenNoChildCategories() {
        tester.setAdminUser()
        val categories = localService.findChildCategories(tester.getSecurityContext(), tester.getFixtures().aCategoryWithProducts().id, null)
        assertThat(categories).isEmpty()
    }

    @Test
    fun findProducts_shouldReturn404ExWhenCategoryNotFound() {
        try {
            tester.setAdminUser()
            localService.findChildProducts(tester.getSecurityContext(), 9999L, null)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
        }
    }

    @Test
    fun findProducts_shouldNotReturnExpiredNorDisabledProducts() {
        tester.setAdminUser()
        val products = localService.findChildProducts(tester.getSecurityContext(), tester.getFixtures().aCategoryWithProducts().id, null)
        assertThat(products).isNotEmpty
        assertThatProductsOf(products).areVisibleProductsOfAChildCategoryWithProducts()
    }

    @Test
    fun findProducts_shouldReturnEmptyListWhenNoChildProducts() {
        tester.setAdminUser()
        val products = localService.findChildProducts(tester.getSecurityContext(), tester.getFixtures().aCategoryWithoutProducts().id, null)
        assertThat(products).isEmpty()
    }

    @Test
    fun findAll_shouldReturnNoneEmptyList() {
        assertThat(localService.findAll(null, null, null, null, null, null)).isNotEmpty
    }

    @Test
    fun findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        val categories = localService.findAll(null, 0, 1, null, null, null)
        assertThat(categories).isNotEmpty
        assertThat(categories).hasSize(1)
    }

    @Test
    fun findAll_withIdSearchParam_shouldReturnResultsWithMatchingId() {
        assertThat(localService.findAll(tester.getFixtures().aCategoryWithoutProducts().id.toString(), null, null, null, null, null)).containsExactly(tester.getFixtures().aCategoryWithoutProducts())
    }

    @Test
    fun findAll_withNameSearchParam_shouldReturnResultsWithMatchingName() {
        assertThat(localService.findAll(tester.getFixtures().aCategoryWithoutProducts().name, null, null, null, null, null)).containsExactly(tester.getFixtures().aCategoryWithoutProducts())
    }

    @Test
    fun modifyCategory_ShouldModifyCategoryAttributesAndPreserveCategoriesWhenNotProvided() {
        tester.setAdminUser()
        val category = Category(tester.getFixtures().aRootCategoryWithChildCategories().id, "New name")
        tester.test_modify(category)
        assertThat(category.name).isEqualTo("New name")
        assertThat(category.childCategories).isNotEmpty
    }

    @Test
    fun modifyCategory_ShouldModifyCategoryAttributesAndPreserveChildProductsWhenNotProvided() {
        tester.setAdminUser()
        val category = Category(tester.getFixtures().aCategoryWithProducts().id, "New name")
        tester.test_modify(category)
        assertThat(category.name).isEqualTo("New name")
        assertThat(category.childProducts).isNotEmpty
    }

    @Test
    fun modifyUnknownCategory_ShouldThrowNotFoundException() {
        try {
            val category = Category(9999L, null)
            localService.modify(tester.getSecurityContext(), category)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
        }
    }

    @Test
    fun countAll() {
        assertThat(localService.count(null)).isGreaterThan(0)
    }

    @Test
    fun countAll_withUnknownSearchCriteria() {
        assertThat(localService.count("666")).isEqualTo(0)
    }

    @Test
    fun create_shouldSetupOwner_for_admin() {
        tester.setAdminUser()
        val category = Category("name", "description", Date(), Date(), false, "test@test.com")
        category.owner = TestCatalog.OWNER

        val actualCategory = tester.test_create(category)

        assertThat(actualCategory).isNotNull
        assertThat(actualCategory.owner).isEqualTo(TestCatalog.OWNER)
    }

    @Test
    fun create_shouldSetupOwner_for_store_admin() {
        tester.setStoreAdminUser()
        val category = Category("name", "")
        val actualCategory = tester.test_create(category)

        assertThat(actualCategory).isNotNull
        assertThat(actualCategory.owner).isEqualTo(TestCatalog.OWNER)
    }

    @Test
    fun create_withoutOwner_shouldThrow_BadRequestEx_for_admin() {
        try {
            tester.setAdminUser()
            val category = Category("name", "description")
            tester.test_create(category)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.BAD_REQUEST)
        }
    }

    @Test
    fun delete_shouldRemove() {
        tester.setStoreAdminUser()
        val category = Category("Test category", "")
        category.owner = TestCatalog.OWNER
        tester.test_delete(category)
        Assertions.assertThat(
            tester.getEntityManager().find(Category::class.java, category.id),
        ).isNull()
    }

    @Test
    fun delete_NotExistingEntry_shouldThrowNotFoundEx() {
        try {
            tester.setStoreAdminUser()
            localService.delete(tester.getSecurityContext(), 666L)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
        }
    }

    @Test
    fun delete_NonManagedEntry_shouldThrowForbiddenEx() {
        try {
            tester.setStoreAdminUser()
            val category = Category("Test category", "")
            category.owner = "test@test.com"
            tester.test_delete(category)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.FORBIDDEN)
        }
    }
}
