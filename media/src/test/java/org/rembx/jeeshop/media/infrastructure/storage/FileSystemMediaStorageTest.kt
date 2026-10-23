package org.rembx.jeeshop.media.infrastructure.storage

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.util.UUID

class FileSystemMediaStorageTest {
    @Test
    fun store_shouldWriteFileThatCanBeFound() {
        val storage = FileSystemMediaStorage()
        val itemType = "test-${UUID.randomUUID()}"
        val itemId = 999L
        val locale = "en_GB"
        val fileName = "test-media.txt"
        val expectedContent = "media content"
        val filePath = storage.getBasePath().resolve(itemType).resolve(itemId.toString()).resolve(locale).resolve(fileName)

        try {
            storage.store(itemType, itemId, locale, fileName, ByteArrayInputStream(expectedContent.toByteArray()))

            assertThat(storage.find(itemType, itemId, locale, fileName)).isEqualTo(filePath.toFile())
            assertThat(Files.readString(filePath)).isEqualTo(expectedContent)
        } finally {
            Files.deleteIfExists(filePath)
            Files.deleteIfExists(filePath.parent)
            Files.deleteIfExists(filePath.parent.parent)
            Files.deleteIfExists(filePath.parent.parent.parent)
        }
    }
}
