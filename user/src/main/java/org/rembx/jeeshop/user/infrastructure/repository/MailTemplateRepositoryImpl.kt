package org.rembx.jeeshop.user.infrastructure.repository

import com.querydsl.core.types.dsl.ComparableExpressionBase
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import io.quarkus.hibernate.orm.PersistenceUnit
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import org.rembx.jeeshop.user.domain.MailTemplateRepository
import org.rembx.jeeshop.user.domain.MailTemplate
import org.rembx.jeeshop.user.domain.QMailTemplate.mailTemplate

@ApplicationScoped
class MailTemplateRepositoryImpl(@PersistenceUnit(UserPersistenceUnit.NAME) private val entityManager: EntityManager) :
    MailTemplateRepository {
    override fun findByNameAndLocale(name: String, locale: String?): MailTemplate? {
        val requestedLocale = locale ?: MailTemplateRepository.DEFAULT_LOCALE
        return JPAQueryFactory(entityManager).selectFrom(mailTemplate).where(
            mailTemplate.name.eq(name)
                .and(mailTemplate.locale.eq(requestedLocale).or(mailTemplate.locale.startsWith(requestedLocale))),
        ).fetchOne()
    }

    override fun findByName(name: String): List<MailTemplate> =
        JPAQueryFactory(entityManager).selectFrom(mailTemplate)
            .where(mailTemplate.name.startsWith(name))
            .fetch()

    override fun findAll(offset: Int?, limit: Int?, orderBy: String?, isDesc: Boolean?): List<MailTemplate> {
        val query = JPAQueryFactory(entityManager).selectFrom(mailTemplate)
        offset?.let { query.offset(it.toLong()) }
        limit?.let { query.limit(it.toLong()) }
        sortBy(orderBy, isDesc, query)
        return query.fetch()
    }

    override fun countAll(): Long = JPAQueryFactory(entityManager).selectFrom(mailTemplate).fetchCount()

    private fun sortBy(orderBy: String?, isDesc: Boolean?, query: JPAQuery<MailTemplate>) {
        val property = sortProperties[orderBy] ?: return
        query.orderBy(if (isDesc == true) property.desc() else property.asc())
    }

    companion object {
        private val sortProperties: Map<String, ComparableExpressionBase<*>> = mapOf(
            "id" to mailTemplate.id,
            "name" to mailTemplate.name,
            "locale" to mailTemplate.locale,
            "creationDate" to mailTemplate.creationDate,
            "updateDate" to mailTemplate.updateDate,
        )
    }
}
