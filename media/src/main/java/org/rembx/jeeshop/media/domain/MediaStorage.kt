package org.rembx.jeeshop.media.domain

import org.rembx.jeeshop.stereotype.Repository
import java.io.File
import java.io.InputStream

@Repository
interface MediaStorage {
    fun store(itemType: String, itemId: Long, locale: String, fileName: String, content: InputStream)

    fun find(itemType: String, itemId: Long, locale: String, fileName: String): File?
}
