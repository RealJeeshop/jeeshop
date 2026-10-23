package org.rembx.jeeshop.catalog.infrastructure.api

import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.rembx.jeeshop.catalog.domain.CatalogItem
import org.rembx.jeeshop.catalog.infrastructure.repository.CatalogPersistenceUnit
import org.rembx.jeeshop.catalog.domain.Category
import org.rembx.jeeshop.catalog.domain.Presentation
import org.rembx.jeeshop.catalog.test.TestCatalog
import java.util.HashMap
import java.util.UUID

class PresentationResourceCT {
    private lateinit var testCatalog: TestCatalog
    private lateinit var entityManager: EntityManager

    companion object {
        private lateinit var entityManagerFactory: EntityManagerFactory

        @BeforeAll
        @JvmStatic
        fun beforeClass() {
            entityManagerFactory = Persistence.createEntityManagerFactory(CatalogPersistenceUnit.NAME)
        }
    }

    @BeforeEach
    fun setup() {
        testCatalog = TestCatalog.getInstance()
        entityManager = entityManagerFactory.createEntityManager()
    }

    @Test
    fun find_shouldReturnPresentation() {
        val presentation = Presentation("en_GB", "presentation1", "short description", "long description")
        val service = PresentationResource<Category>(entityManager, null).init(Category::class.java, null, "fr_FR", presentation)

        assertThat(service.find()).isEqualTo(presentation)
    }

    @Test
    fun createLocalizedPresentation_shouldPersistGivenPresentationAndLinkItToParentCatalogItem() {
        val parentCatalogItem = testCatalog.aCategoryWithoutPresentation()

        val service = PresentationResource<Category>(entityManager, null).init(Category::class.java, parentCatalogItem, "fr_FR", null)

        entityManager.transaction.begin()
        val presentation = Presentation("fr_FR", "presentation test", "testShortDesc", "testLongDesc")
        service.createLocalizedPresentation(presentation)
        entityManager.transaction.commit()

        assertThat(presentation.id).isNotNull
        assertThat(entityManager.find(Presentation::class.java, presentation.id)).isNotNull

        val updatedItem = entityManager.find(Category::class.java, parentCatalogItem.id)
        assertThat(updatedItem).isNotNull
        assertThat(updatedItem!!.presentationByLocale?.get("fr_FR")).isNotNull
        assertThat(updatedItem.presentationByLocale?.get("fr_FR")!!.locale).isEqualTo("fr_FR")

        removePersistedTestData(parentCatalogItem, presentation)
    }

    @Test
    fun modifyLocalizedPresentation_shouldUpdateGivenPresentation() {
        val presentation = createTestPresentation()

        val service = PresentationResource<Category>(entityManager, null).init(Category::class.java, null, "fr_FR", presentation)

        val uuid = UUID.randomUUID()
        presentation.shortDescription = uuid.toString()

        entityManager.transaction.begin()
        service.modifyLocalizedPresentation(presentation)
        entityManager.transaction.commit()

        assertThat(entityManager.find(Presentation::class.java, presentation.id)!!.shortDescription).isEqualTo(uuid.toString())

        removePersistedTestData(null, presentation)
    }

    @Test
    fun deleteGivenPresentation_shouldRemoveIt() {
        val parentCatalogItem = testCatalog.aCategoryWithoutPresentation()

        val presentation = createTestPresentation()
        parentCatalogItem.presentationByLocale = HashMap()
        parentCatalogItem.presentationByLocale?.put("fr_FR", presentation)

        val service = PresentationResource<Category>(entityManager, null).init(Category::class.java, parentCatalogItem, "fr_FR", presentation)

        entityManager.transaction.begin()
        service.delete()
        entityManager.transaction.commit()

        assertThat(entityManager.find(Presentation::class.java, presentation.id)).isNull()

        val updatedItem = entityManager.find(Category::class.java, parentCatalogItem.id)
        assertThat(updatedItem!!.presentationByLocale?.get("fr_FR")).isNull()
    }

    private fun createTestPresentation(): Presentation {
        entityManager.transaction.begin()
        val presentation = Presentation("fr_FR", "presentation test", "testShortDesc", "testLongDesc")
        entityManager.persist(presentation)
        entityManager.transaction.commit()
        return presentation
    }

    private fun removePersistedTestData(parentCatalogItem: CatalogItem?, presentation: Presentation) {
        entityManager.transaction.begin()
        entityManager.remove(presentation)
        if (parentCatalogItem != null) {
            parentCatalogItem.presentationByLocale = HashMap()
        }
        entityManager.transaction.commit()
    }
}
