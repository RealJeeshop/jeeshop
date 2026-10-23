package org.rembx.jeeshop.user.infrastructure.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import io.quarkus.hibernate.orm.PersistenceUnit
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import org.rembx.jeeshop.user.domain.RoleRepository
import org.rembx.jeeshop.user.domain.QRole.role
import org.rembx.jeeshop.user.domain.Role
import org.rembx.jeeshop.user.domain.RoleName

@ApplicationScoped
class RoleRepositoryImpl(@PersistenceUnit(UserPersistenceUnit.NAME) private val entityManager: EntityManager) :
    RoleRepository {
    override fun findByName(name: RoleName): Role? =
        JPAQueryFactory(entityManager).selectFrom(role).where(role.name.eq(name)).fetchOne()
}
