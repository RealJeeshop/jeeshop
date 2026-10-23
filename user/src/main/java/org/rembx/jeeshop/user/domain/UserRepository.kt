package org.rembx.jeeshop.user.domain

import org.rembx.jeeshop.stereotype.Repository

@Repository
interface UserRepository {
    fun findByLogin(login: String?): User?
    fun findAll(offset: Int?, limit: Int?, orderBy: String?, isDesc: Boolean?): List<User>
    fun countAll(): Long
    fun countBySearchCriteria(searchCriteria: String): Long
    fun findBySearchCriteria(
        searchCriteria: String,
        offset: Int?,
        limit: Int?,
        orderBy: String?,
        isDesc: Boolean?,
    ): List<User>
}
