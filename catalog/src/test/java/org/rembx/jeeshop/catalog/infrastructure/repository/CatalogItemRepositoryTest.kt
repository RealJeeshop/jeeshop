package org.rembx.jeeshop.catalog.infrastructure.repository

import jakarta.persistence.EntityManager
import jakarta.ws.rs.core.Response
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.rembx.jeeshop.catalog.domain.Catalog
import org.rembx.jeeshop.catalog.domain.CatalogItem
import org.rembx.jeeshop.rest.WebApplicationException

class CatalogItemRepositoryTest {
    private lateinit var instance: CatalogItemRepositoryImpl

    private val visibleCatalogItem = object : CatalogItem() {
        override fun isVisible(): Boolean = true
    }

    @BeforeEach
    fun setup() {
        instance = CatalogItemRepositoryImpl(null as EntityManager)
    }

    @Test
    fun find_VisibleCatalogItem_ShouldReturnExpectedProduct() {
        assertThat(instance.filterVisible(visibleCatalogItem, null)).isEqualTo(visibleCatalogItem)
    }

    @Test
    fun find_NotVisibleCatalogItem_ShouldThrowForbiddenException() {
        try {
            instance.filterVisible(Catalog(), null)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            Assertions.assertEquals(Response.Status.FORBIDDEN, e.response.statusInfo)
        }
    }

    @Test
    fun find_NullCatalogItem_ShouldThrowNotFoundException() {
        try {
            instance.filterVisible(null, null)
            fail("should have thrown ex")
        } catch (e: WebApplicationException) {
            Assertions.assertEquals(Response.Status.NOT_FOUND, e.response.statusInfo)
        }
    }
}
