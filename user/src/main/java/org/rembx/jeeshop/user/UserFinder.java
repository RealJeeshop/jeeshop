package org.rembx.jeeshop.user;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.rembx.jeeshop.user.model.User;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.rembx.jeeshop.user.model.QUser.user;

/**
 * User finder utility
 */
public class UserFinder {

    @PersistenceContext(unitName = UserPersistenceUnit.NAME)
    private EntityManager entityManager;

    public UserFinder() {
    }

    private static final Map<String, ComparableExpressionBase<?>> sortProperties = new HashMap<String, ComparableExpressionBase<?>>() {{
        put("id", user.id);
        put("login", user.login);
        put("gender", user.gender);
        put("firstname", user.firstname);
        put("lastname", user.lastname);
        put("disabled", user.disabled);
        put("activated", user.activated);
        put("newslettersSubscribed", user.newslettersSubscribed);
        put("birthDate", user.birthDate);
        put("phoneNumber", user.phoneNumber);
        put("preferredLocale", user.preferredLocale);
        put("creationDate", user.creationDate.stringValue());
        put("updateDate", user.updateDate);
    }};

    public UserFinder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public User findByLogin(String login) {
        return new JPAQueryFactory(entityManager)
                .selectFrom(user).where(
                        user.login.eq(login))
                .fetchOne();
    }

    public List<User> findAll(Integer offset, Integer limit, String orderBy, Boolean isDesc) {
        JPAQuery<User> query = new JPAQueryFactory(entityManager).selectFrom(user);

        if (offset != null)
            query.offset(offset);
        if (limit != null)
            query.limit(limit);

        sortBy(orderBy, isDesc, query);

        return query.fetch();

    }

    public Long countAll() {
        return new JPAQueryFactory(entityManager)
                .selectFrom(user)
                .fetchCount();
    }

    public Long countBySearchCriteria(String searchCriteria) {
        JPAQuery<User> query = new JPAQueryFactory(entityManager).selectFrom(user)
                .where(buildSearchPredicate(searchCriteria));
        return query.fetchCount();
    }

    public List<User> findBySearchCriteria(String searchCriteria, Integer offset, Integer limit, String orderBy, Boolean isDesc) {
        JPAQuery<User> query = new JPAQueryFactory(entityManager).selectFrom(user)
                .where(buildSearchPredicate(searchCriteria));

        if (offset != null)
            query.offset(offset);
        if (limit != null)
            query.limit(limit);

        sortBy(orderBy, isDesc, query);

        return query.fetch();
    }

    private BooleanExpression buildSearchPredicate(String search) {
        return user.login.containsIgnoreCase(search)
                .or(user.firstname.containsIgnoreCase(search))
                .or(user.lastname.containsIgnoreCase(search));
    }

    private void sortBy(String orderby, Boolean isDesc, JPAQuery<User> query) {
        if (orderby != null && sortProperties.containsKey(orderby)) {
            if (isDesc) {
                query.orderBy(sortProperties.get(orderby).desc());
            } else {
                query.orderBy(sortProperties.get(orderby).asc());
            }
        }
    }

}
