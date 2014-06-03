package org.rembx.jeeshop.user;

import com.mysema.query.jpa.impl.JPAQuery;
import org.rembx.jeeshop.user.model.User;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.rembx.jeeshop.user.model.QUser.user;

/**
 * Created by remi on 01/06/14.
 */
public class UserFinder {

    @PersistenceContext(unitName = UserPersistenceUnit.NAME)
    private EntityManager entityManager;

    public UserFinder() {
    }

    public UserFinder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public User findByLogin(String login) {
        return new JPAQuery(entityManager)
                .from(user).where(
                        user.login.eq(login))
                .singleResult(user);
    }

}
