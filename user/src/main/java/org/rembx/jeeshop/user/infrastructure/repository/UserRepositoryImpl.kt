package org.rembx.jeeshop.user.infrastructure.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.ComparableExpressionBase
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import io.quarkus.hibernate.orm.PersistenceUnit
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import org.rembx.jeeshop.user.domain.UserRepository
import org.rembx.jeeshop.user.domain.QUser.user
import org.rembx.jeeshop.user.domain.User

@ApplicationScoped
class UserRepositoryImpl(@PersistenceUnit(UserPersistenceUnit.NAME) private val entityManager: EntityManager) :
    UserRepository {
    override fun findByLogin(login: String?): User? =
        JPAQueryFactory(entityManager).selectFrom(user).where(user.login.eq(login)).fetchOne()

    override fun findAll(offset: Int?, limit: Int?, orderBy: String?, isDesc: Boolean?): List<User> {
        val query = JPAQueryFactory(entityManager).selectFrom(user)
        offset?.let { query.offset(it.toLong()) }
        limit?.let { query.limit(it.toLong()) }
        sortBy(orderBy, isDesc, query)
        return query.fetch()
    }

    override fun countAll(): Long = JPAQueryFactory(entityManager).selectFrom(user).fetchCount()

    override fun countBySearchCriteria(searchCriteria: String): Long =
        JPAQueryFactory(entityManager).selectFrom(user)
            .where(buildSearchPredicate(searchCriteria))
            .fetchCount()

    override fun findBySearchCriteria(
        searchCriteria: String,
        offset: Int?,
        limit: Int?,
        orderBy: String?,
        isDesc: Boolean?,
    ): List<User> {
        val query = JPAQueryFactory(entityManager).selectFrom(user)
            .where(buildSearchPredicate(searchCriteria))
        offset?.let { query.offset(it.toLong()) }
        limit?.let { query.limit(it.toLong()) }
        sortBy(orderBy, isDesc, query)
        return query.fetch()
    }

    private fun buildSearchPredicate(search: String): BooleanExpression =
        user.login.containsIgnoreCase(search)
            .or(user.firstname.containsIgnoreCase(search))
            .or(user.lastname.containsIgnoreCase(search))

    private fun sortBy(orderBy: String?, isDesc: Boolean?, query: JPAQuery<User>) {
        val property = sortProperties[orderBy] ?: return
        query.orderBy(if (isDesc == true) property.desc() else property.asc())
    }

    companion object {
        private val sortProperties: Map<String, ComparableExpressionBase<*>> = mapOf(
            "id" to user.id,
            "login" to user.login,
            "gender" to user.gender,
            "firstname" to user.firstname,
            "lastname" to user.lastname,
            "disabled" to user.disabled,
            "activated" to user.activated,
            "newslettersSubscribed" to user.newslettersSubscribed,
            "birthDate" to user.birthDate,
            "phoneNumber" to user.phoneNumber,
            "preferredLocale" to user.preferredLocale,
            "creationDate" to user.creationDate.stringValue(),
            "updateDate" to user.updateDate,
        )
    }
}
