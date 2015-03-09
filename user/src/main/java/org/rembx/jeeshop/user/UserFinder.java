package org.rembx.jeeshop.user;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.expr.ComparableExpressionBase;
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
        return new JPAQuery(entityManager)
                .from(user).where(
                        user.login.eq(login))
                .singleResult(user);
    }

    public List<User> findAll(Integer offset, Integer limit, String orderBy, Boolean isDesc) {
        JPAQuery query = new JPAQuery(entityManager).from(user);

        if (offset != null)
            query.offset(offset);
        if (limit != null)
            query.limit(limit);

        sortBy(orderBy, isDesc, query);

        return query.list(user);

    }

    public Long countAll() {
        JPAQuery query = new JPAQuery(entityManager).from(user);
        return query.count();
    }

    public Long countBySearchCriteria(String searchCriteria) {
        JPAQuery query = new JPAQuery(entityManager)
                .from(user)
                .where(buildSearchPredicate(searchCriteria));
        return query.count();
    }

    public List<User> findBySearchCriteria(String searchCriteria, Integer offset, Integer limit,String orderBy, Boolean isDesc) {
        JPAQuery query = new JPAQuery(entityManager).from(user)
                .where(buildSearchPredicate(searchCriteria));

        if (offset != null)
            query.offset(offset);
        if (limit != null)
            query.limit(limit);

        sortBy(orderBy, isDesc, query);

        return query.list(user);
    }

    private BooleanExpression buildSearchPredicate(String search) {
        return  user.login.containsIgnoreCase(search)
                .or(user.firstname.containsIgnoreCase(search))
                .or(user.lastname.containsIgnoreCase(search));
    }

    private void sortBy(String orderby, Boolean isDesc, JPAQuery query) {
        if (orderby != null && sortProperties.containsKey(orderby)) {
            if (isDesc) {
                query.orderBy(sortProperties.get(orderby).desc());
            } else {
                query.orderBy(sortProperties.get(orderby).asc());
            }
        }
    }

}
