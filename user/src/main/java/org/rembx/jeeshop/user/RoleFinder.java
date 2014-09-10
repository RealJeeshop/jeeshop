package org.rembx.jeeshop.user;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import org.rembx.jeeshop.user.model.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

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
        return new JPAQuery(entityManager)
                .from(role).where(
                        role.name.eq(name))
                .singleResult(role);
    }

}
