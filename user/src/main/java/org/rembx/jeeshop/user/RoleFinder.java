package org.rembx.jeeshop.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.quarkus.hibernate.orm.PersistenceUnit;
import org.rembx.jeeshop.user.model.Role;
import org.rembx.jeeshop.user.model.RoleName;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import javax.persistence.EntityManager;

import static org.rembx.jeeshop.user.model.QRole.role;

/**
 * User finder utility
 */
public class RoleFinder {

    @PersistenceUnit(UserPersistenceUnit.NAME)
    EntityManager entityManager;

    public RoleFinder() {
    }

    public RoleFinder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Role findByName(RoleName name) {
        return new JPAQueryFactory(entityManager)
                .selectFrom(role).where(
                        role.name.eq(name))
                .fetchOne();
    }

}
