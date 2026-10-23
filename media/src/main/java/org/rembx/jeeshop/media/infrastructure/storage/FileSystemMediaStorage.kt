package org.rembx.jeeshop.media.infrastructure.storage

import jakarta.enterprise.context.ApplicationScoped
import org.rembx.jeeshop.media.domain.MediaStorage
import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@ApplicationScoped
class FileSystemMediaStorage : MediaStorage {
    override fun store(itemType: String, itemId: Long, locale: String, fileName: String, content: InputStream) {
        val itemBasePath = getBasePath().resolve(itemType).resolve(itemId.toString()).resolve(locale)
        Files.createDirectories(itemBasePath)
        val filePath = itemBasePath.resolve(fileName)
        Files.copy(content, filePath, StandardCopyOption.REPLACE_EXISTING)
        LOG.info("File written to {}", filePath)
    }

    override fun find(itemType: String, itemId: Long, locale: String, fileName: String): File? {
        val filePath = getBasePath().resolve(itemType).resolve(itemId.toString()).resolve(locale).resolve(fileName)
        return if (Files.exists(filePath)) filePath.toFile() else null
    }

    internal fun getBasePath(): Path =
        when {
            !System.getenv(OPENSHIFT_DATA_DIR).isNullOrEmpty() ->
                Paths.get(System.getenv(OPENSHIFT_DATA_DIR)).resolve(JEESHOP_MEDIA_DIR)
            !System.getenv(JEESHOP_DATA_DIR).isNullOrEmpty() ->
                Paths.get(System.getenv(JEESHOP_DATA_DIR)).resolve(JEESHOP_MEDIA_DIR)
            else -> Paths.get(JEESHOP_MEDIA_DIR)
        }

    companion object {
        const val JEESHOP_MEDIA_DIR = "jeeshop-media"
        const val JEESHOP_DATA_DIR = "JEESHOP_DATA_DIR"
        const val OPENSHIFT_DATA_DIR = "OPENSHIFT_DATA_DIR"
        private val LOG = LoggerFactory.getLogger(FileSystemMediaStorage::class.java)
    }
}
