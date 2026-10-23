package org.rembx.jeeshop.media.infrastructure.api

import jakarta.ws.rs.core.Response
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.rembx.jeeshop.media.domain.MediaStorage
import org.rembx.jeeshop.rest.WebApplicationException
import java.io.File
import java.io.InputStream

class MediasTest {
    private lateinit var medias: Medias
    private lateinit var mediaStorage: TestMediaStorage

    @BeforeEach
    fun setup() {
        mediaStorage = TestMediaStorage()
        medias = Medias(mediaStorage)
    }

    @Test
    fun get_shouldReturnFile_whenThereIsAFileWithPathMatchingGivenParams() {
        val file = File("testMedias.test")
        mediaStorage.file = file

        assertThat(medias.get("categories", 999L, "en_GB", "testMedias.test")).isEqualTo(file)
        assertThat(mediaStorage.requestedPath).isEqualTo(
            Request("categories", 999L, "en_GB", "testMedias.test"),
        )
    }

    @Test
    fun get_shouldThrowNotFound_whenThereAreNoFileMatchingGivenParams() {
        try {
            medias.get("categories", 999L, "en_GB", "unknown.test")
            throw AssertionError("Should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.status).isEqualTo(Response.Status.NOT_FOUND.statusCode)
        }
    }

    private class TestMediaStorage(var file: File? = null) : MediaStorage {
        var requestedPath: Request? = null

        override fun store(itemType: String, itemId: Long, locale: String, fileName: String, content: InputStream) = Unit

        override fun find(itemType: String, itemId: Long, locale: String, fileName: String): File? {
            requestedPath = Request(itemType, itemId, locale, fileName)
            return file
        }
    }

    private data class Request(val itemType: String, val itemId: Long, val locale: String, val fileName: String)
}
