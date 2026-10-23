package org.rembx.jeeshop.catalog.infrastructure.repository

import com.querydsl.core.types.EntityPath
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.ComparableExpressionBase
import com.querydsl.core.types.dsl.ListPath
import com.querydsl.core.types.dsl.SimpleExpression
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import io.quarkus.hibernate.orm.PersistenceUnit
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.apache.commons.lang3.math.NumberUtils
import org.rembx.jeeshop.catalog.domain.CatalogItemRepository
import org.rembx.jeeshop.catalog.domain.Catalog
import org.rembx.jeeshop.catalog.domain.CatalogItem
import org.rembx.jeeshop.catalog.domain.Category
import org.rembx.jeeshop.catalog.domain.Product
import org.rembx.jeeshop.catalog.domain.QCatalogItem
import org.rembx.jeeshop.rest.WebApplicationException
import org.rembx.jeeshop.role.JeeshopRoles
import java.util.Date

/** Utility class for common finders on CatalogItem entities. */
@ApplicationScoped
class CatalogItemRepositoryImpl(
    @PersistenceUnit(value = CatalogPersistenceUnit.NAME)
    private val entityManager: EntityManager
) : CatalogItemRepository {
    override fun <T : CatalogItem> findVisibleCatalogItems(entityPath: EntityPath<T>, items: List<T>, locale: String?): List<T> {
        val qCatalogItem = QCatalogItem(entityPath)
        val now = Date()
        val results = JPAQueryFactory(entityManager).selectFrom(entityPath).where(
            qCatalogItem.disabled.isFalse,
            qCatalogItem.endDate.after(now).or(qCatalogItem.endDate.isNull),
            qCatalogItem.startDate.before(now).or(qCatalogItem.startDate.isNull),
            qCatalogItem.`in`(items)
        ).fetch()
        results.forEach {
            it.setLocalizedPresentation(locale)
            mapCatalogItemChildrenPresentation(it, locale)
        }
        return results
    }

    override fun <T : CatalogItem> findByOwner(entityPath: EntityPath<T>, owner: String?): List<T> {
        val qCatalogItem = QCatalogItem(entityPath)
        return JPAQueryFactory(entityManager).selectFrom(entityPath).where(qCatalogItem.owner.eq(owner)).fetch()
    }

    override fun <T : CatalogItem> findAll(
        entityPath: EntityPath<T>,
        offset: Int?,
        limit: Int?,
        orderBy: String?,
        isDesc: Boolean?,
        locale: String?
    ): List<T> {
        val qCatalogItem = QCatalogItem(entityPath)
        val query = JPAQueryFactory(entityManager).selectFrom(entityPath)
        addOffsetAndLimitToQuery(offset, limit, query, orderBy, isDesc, qCatalogItem)
        val items = query.fetch()
        if (locale == null) return items
        return items.onEach {
            it.setLocalizedPresentation(locale)
            mapCatalogItemChildrenPresentation(it, locale)
        }
    }

    override fun <T : CatalogItem, P : CatalogItem> findForeignHolder(
        holderPath: EntityPath<P>,
        relation: ListPath<T, out SimpleExpression<T>>,
        item: T
    ): List<P> = JPAQueryFactory(entityManager).selectFrom(holderPath).where(relation.contains(item)).fetch()

    override fun <T : CatalogItem> findBySearchCriteria(
        entityPath: EntityPath<T>,
        searchCriteria: String,
        offset: Int?,
        limit: Int?,
        orderBy: String?,
        isDesc: Boolean?,
        locale: String?
    ): List<T> {
        val qCatalogItem = QCatalogItem(entityPath)
        val query = JPAQueryFactory(entityManager).selectFrom(entityPath)
            .where(buildSearchPredicate(searchCriteria, qCatalogItem))
        addOffsetAndLimitToQuery(offset, limit, query, orderBy, isDesc, qCatalogItem)
        val items = query.fetch()
        if (locale == null) return items
        return items.onEach {
            it.setLocalizedPresentation(locale)
            mapCatalogItemChildrenPresentation(it, locale)
        }
    }

    override fun countAll(entityPath: EntityPath<out CatalogItem>): Long =
        JPAQueryFactory(entityManager).selectFrom(QCatalogItem(entityPath)).fetchCount()

    override fun countBySearchCriteria(entityPath: EntityPath<out CatalogItem>, searchCriteria: String): Long {
        val qCatalogItem = QCatalogItem(entityPath)
        return JPAQueryFactory(entityManager).selectFrom(qCatalogItem)
            .where(buildSearchPredicate(searchCriteria, qCatalogItem)).fetchCount()
    }

    override fun <T : CatalogItem> filterVisible(catalogItem: T?, locale: String?): T {
        if (catalogItem == null) throw WebApplicationException(Response.Status.NOT_FOUND)
        if (!catalogItem.isVisible()) throw WebApplicationException(Response.Status.FORBIDDEN)
        catalogItem.setLocalizedPresentation(locale)
        mapCatalogItemChildrenPresentation(catalogItem, locale)
        return catalogItem
    }

    private fun mapCatalogItemChildrenPresentation(catalogItem: CatalogItem, locale: String?) {
        if (locale == null) return
        when (catalogItem) {
            is Catalog -> catalogItem.rootCategories!!.forEach { it.setLocalizedPresentation(locale) }
            is Category -> catalogItem.childCategories!!.forEach { it.setLocalizedPresentation(locale) }
            is Product -> {
                catalogItem.childSKUs!!.forEach { it.setLocalizedPresentation(locale) }
                catalogItem.discounts!!.forEach { it.setLocalizedPresentation(locale) }
            }
        }
    }

    private fun <T : CatalogItem> addOffsetAndLimitToQuery(
        offset: Int?,
        limit: Int?,
        query: JPAQuery<T>,
        orderBy: String?,
        isDesc: Boolean?,
        qCatalogItem: QCatalogItem
    ) {
        if (offset != null) query.offset(offset.toLong())
        if (limit != null) query.limit(limit.toLong())
        sortBy(orderBy, isDesc, query, qCatalogItem)
    }

    private fun buildSearchPredicate(search: String, qCatalogItem: QCatalogItem): BooleanExpression {
        var predicate = qCatalogItem.name.containsIgnoreCase(search)
            .or(qCatalogItem.description.containsIgnoreCase(search))
        if (NumberUtils.isNumber(search)) predicate = qCatalogItem.id.eq(search.toLong())
        return predicate
    }

    private fun <T : CatalogItem> sortBy(
        orderBy: String?,
        isDesc: Boolean?,
        query: JPAQuery<T>,
        qCatalogItem: QCatalogItem
    ) {
        val properties: Map<String, ComparableExpressionBase<*>> = mapOf(
            "id" to qCatalogItem.id,
            "name" to qCatalogItem.name,
            "description" to qCatalogItem.description,
            "startDate" to qCatalogItem.startDate,
            "endDate" to qCatalogItem.endDate,
            "disabled" to qCatalogItem.disabled
        )
        val property = properties[orderBy] ?: return
        query.orderBy(if (isDesc == true) property.desc() else property.asc())
    }

    override fun <T : CatalogItem> findOne(entityPath: EntityPath<T>, itemId: Long, securityIdentity: SecurityContext): T? {
        val qCatalogItem = QCatalogItem(entityPath)
        val query = JPAQueryFactory(entityManager).selectFrom(entityPath).where(qCatalogItem.id.eq(itemId))
        return when {
            securityIdentity.isUserInRole(JeeshopRoles.ADMIN) -> query.fetchOne()
            securityIdentity.isUserInRole(JeeshopRoles.STORE_ADMIN) ->
                query.where(qCatalogItem.owner.eq(securityIdentity.userPrincipal.name)).fetchOne()
            else -> throw WebApplicationException(Response.Status.FORBIDDEN)
        }
    }
}
