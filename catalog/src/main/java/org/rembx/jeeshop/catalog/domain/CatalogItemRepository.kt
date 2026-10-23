package org.rembx.jeeshop.catalog.domain

import com.querydsl.core.types.EntityPath
import com.querydsl.core.types.dsl.ListPath
import com.querydsl.core.types.dsl.SimpleExpression
import jakarta.ws.rs.core.SecurityContext
import org.rembx.jeeshop.stereotype.Repository

/** Utility class for common finders on CatalogItem entities. */
@Repository
interface CatalogItemRepository{
    fun <T : CatalogItem> findVisibleCatalogItems(entityPath: EntityPath<T>, items: List<T>, locale: String?): List<T>

    fun <T : CatalogItem> findByOwner(entityPath: EntityPath<T>, owner: String?): List<T>
    fun <T : CatalogItem> findAll(
        entityPath: EntityPath<T>,
        offset: Int?,
        limit: Int?,
        orderBy: String?,
        isDesc: Boolean?,
        locale: String?
    ): List<T> 

    fun <T : CatalogItem, P : CatalogItem> findForeignHolder(
        holderPath: EntityPath<P>,
        relation: ListPath<T, out SimpleExpression<T>>,
        item: T
    ): List<P> 

    fun <T : CatalogItem> findBySearchCriteria(
        entityPath: EntityPath<T>,
        searchCriteria: String,
        offset: Int?,
        limit: Int?,
        orderBy: String?,
        isDesc: Boolean?,
        locale: String?
    ): List<T> 

    fun countAll(entityPath: EntityPath<out CatalogItem>): Long

    fun countBySearchCriteria(entityPath: EntityPath<out CatalogItem>, searchCriteria: String): Long

    fun <T : CatalogItem> filterVisible(catalogItem: T?, locale: String?): T 
    

    fun <T : CatalogItem> findOne(entityPath: EntityPath<T>, itemId: Long, securityIdentity: SecurityContext): T?
}
