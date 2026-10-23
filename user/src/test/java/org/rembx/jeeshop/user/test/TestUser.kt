package org.rembx.jeeshop.user.test

import jakarta.persistence.Persistence
import org.rembx.jeeshop.user.domain.Address
import org.rembx.jeeshop.user.domain.Role
import org.rembx.jeeshop.user.domain.RoleName
import org.rembx.jeeshop.user.domain.User
import org.rembx.jeeshop.user.infrastructure.repository.UserPersistenceUnit
import java.sql.Timestamp
import java.time.ZonedDateTime
import java.util.Date

class TestUser private constructor() {
    fun firstUser(): User = user1

    companion object {
        private var instance: TestUser? = null
        private lateinit var user1: User

        private val now: Date = Timestamp.from(ZonedDateTime.now().toInstant())
        private val yesterday: Date = toDatabaseTimestamp(ZonedDateTime.now().minusDays(1))

        private fun toDatabaseTimestamp(dateTime: ZonedDateTime): Timestamp =
            Timestamp.from(dateTime.toInstant()).apply { nanos = nanos / 1_000 * 1_000 }

        @JvmStatic
        fun getInstance(): TestUser {
            instance?.let { return it }
            val entityManager = Persistence.createEntityManagerFactory(UserPersistenceUnit.NAME).createEntityManager()
            entityManager.transaction.begin()

            val address = Address("21 Blue street", "Chicago", "78801", "John", "Doe", "M.", null, "FRA")
            user1 = User("test@test.com", "test", "John", "Doe", "+33616161616", null, yesterday, "fr_FR", null)
            user1.gender = "M."

            entityManager.persist(address)
            entityManager.persist(user1)
            entityManager.persist(Role(RoleName.admin))
            entityManager.persist(Role(RoleName.user))
            entityManager.transaction.commit()

            return TestUser().also {
                instance = it
                entityManager.close()
            }
        }
    }
}
