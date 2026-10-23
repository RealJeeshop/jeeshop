package org.rembx.jeeshop.user.domain

import org.rembx.jeeshop.stereotype.Repository

@Repository
interface MailTemplateRepository {
    fun findByNameAndLocale(name: String, locale: String?): MailTemplate?
    fun findByName(name: String): List<MailTemplate>
    fun findAll(offset: Int?, limit: Int?, orderBy: String?, isDesc: Boolean?): List<MailTemplate>
    fun countAll(): Long

    companion object {
        const val DEFAULT_LOCALE = "en_GB"
    }
}
