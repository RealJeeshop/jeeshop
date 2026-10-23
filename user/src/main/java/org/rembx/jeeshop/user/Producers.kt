package org.rembx.jeeshop.user

import io.quarkus.hibernate.orm.PersistenceUnit
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Default
import jakarta.enterprise.inject.Produces
import jakarta.persistence.EntityManagerFactory
import org.rembx.jeeshop.user.infrastructure.repository.UserPersistenceUnit

class Producers {
    @Produces
    @ApplicationScoped
    @Default
    fun produceService(
        @PersistenceUnit(UserPersistenceUnit.NAME) entityManagerFactory: EntityManagerFactory,
    ): EntityManagerFactory = entityManagerFactory
}
