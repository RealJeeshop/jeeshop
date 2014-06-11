package org.rembx.jeeshop.user;

import com.mysema.query.jpa.impl.JPAQuery;
import org.rembx.jeeshop.user.model.User;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.rembx.jeeshop.user.model.QUser.user;

/**
 * User finder utility
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

    public List<User> findAll(Integer offset, Integer limit) {
        JPAQuery query = new JPAQuery(entityManager).from(user);

        if (offset != null)
            query.offset(offset);
        if (limit != null)
            query.limit(limit);

        return query.list(user);

    }
}
