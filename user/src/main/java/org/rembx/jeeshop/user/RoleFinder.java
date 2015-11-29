package org.rembx.jeeshop.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.rembx.jeeshop.user.model.Role;
import org.rembx.jeeshop.user.model.RoleName;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.rembx.jeeshop.user.model.QRole.role;

/**
 * User finder utility
 */
public class RoleFinder {

    @PersistenceContext(unitName = UserPersistenceUnit.NAME)
    private EntityManager entityManager;

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
