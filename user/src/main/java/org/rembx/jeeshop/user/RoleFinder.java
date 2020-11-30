package org.rembx.jeeshop.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.quarkus.hibernate.orm.PersistenceUnit;
import org.rembx.jeeshop.user.model.Role;
import org.rembx.jeeshop.user.model.RoleName;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;

import static org.rembx.jeeshop.user.model.QRole.role;

/**
 * User finder utility
 */
@ApplicationScoped
public class RoleFinder {

    private EntityManager entityManager;

    RoleFinder(@PersistenceUnit(UserPersistenceUnit.NAME) EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Role findByName(RoleName name) {
        return new JPAQueryFactory(entityManager)
                .selectFrom(role).where(
                        role.name.eq(name))
                .fetchOne();
    }

}
