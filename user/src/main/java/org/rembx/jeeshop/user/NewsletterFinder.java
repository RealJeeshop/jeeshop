package org.rembx.jeeshop.user;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang.StringUtils;
import org.rembx.jeeshop.user.model.Newsletter;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.rembx.jeeshop.user.model.QNewsletter.newsletter;

/**
 * Newsletter finder utility
 */
public class NewsletterFinder {

    private final static String DEFAULT_LOCALE = "en_GB";

    @PersistenceContext(unitName = UserPersistenceUnit.NAME)
    private EntityManager entityManager;

    private static final Map<String, ComparableExpressionBase<?>> sortProperties = new HashMap<String, ComparableExpressionBase<?>>() {{
        put("id", newsletter.id);
        put("name", newsletter.name);
        put("creationDate", newsletter.creationDate);
        put("updateDate", newsletter.updateDate);
        put("dueDate", newsletter.dueDate);
    }};

    public NewsletterFinder() {
    }

    public NewsletterFinder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Newsletter findByNameAndTemplateLocale(String name, String locale) {

        if (locale == null) {
            locale = DEFAULT_LOCALE;
        }

        return new JPAQueryFactory(entityManager)
                .selectFrom(newsletter).where(
                )
        .fetchOne();
    }

    public List<Newsletter> findAll(Integer offset, Integer limit, String orderBy, Boolean isDesc, String name, String locale) {
        JPAQuery<Newsletter> query = new JPAQueryFactory(entityManager).selectFrom(newsletter);

        if (offset != null)
            query.offset(offset);
        if (limit != null)
            query.limit(limit);

        BooleanBuilder where = new BooleanBuilder();

        if (StringUtils.isNotEmpty(name))
            where.and(newsletter.name.eq(name));
        if (StringUtils.isNotEmpty(locale))
            where.and(newsletter.mailTemplate.locale.eq(locale).or(newsletter.mailTemplate.locale.startsWith(locale)));

        query.where(where);

        sortBy(orderBy, isDesc, query);

        return query.fetch();

    }

    public Long countAll() {
        JPAQuery query = new JPAQueryFactory(entityManager).selectFrom(newsletter);
        return query.fetchCount();
    }

    private void sortBy(String orderby, Boolean isDesc, JPAQuery<Newsletter> query) {
        if (orderby != null && sortProperties.containsKey(orderby)) {
            if (isDesc) {
                query.orderBy(sortProperties.get(orderby).desc());
            } else {
                query.orderBy(sortProperties.get(orderby).asc());
            }
        }
    }
}
