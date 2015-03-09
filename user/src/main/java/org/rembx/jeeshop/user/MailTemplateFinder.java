package org.rembx.jeeshop.user;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.ComparableExpressionBase;
import org.rembx.jeeshop.user.model.MailTemplate;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.rembx.jeeshop.user.model.QMailTemplate.mailTemplate;
import static org.rembx.jeeshop.user.model.QUser.user;

/**
 * Newsletter finder utility
 */
public class MailTemplateFinder {

    public final static String DEFAULT_LOCALE = "en_GB";

    @PersistenceContext(unitName = UserPersistenceUnit.NAME)
    private EntityManager entityManager;

    private static final Map<String, ComparableExpressionBase<?>> sortProperties = new HashMap<String, ComparableExpressionBase<?>>() {{
        put("id", mailTemplate.id);
        put("name", mailTemplate.name);
        put("locale", mailTemplate.locale);
        put("creationDate", mailTemplate.creationDate);
        put("updateDate", mailTemplate.updateDate);
    }};

    public MailTemplateFinder() {
    }

    public MailTemplateFinder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public MailTemplate findByNameAndLocale(String name, String locale) {

        if (locale == null){
            locale = DEFAULT_LOCALE;
        }

        return new JPAQuery(entityManager)
                .from(mailTemplate).where(
                        mailTemplate.name.eq(name)
                .and(mailTemplate.locale.eq(locale).or(mailTemplate.locale.startsWith(locale))))
                .singleResult(mailTemplate);
    }

    public List<MailTemplate> findByName(String name) {

        return new JPAQuery(entityManager)
                .from(mailTemplate).where(
                        mailTemplate.name.startsWith(name))
                .list(mailTemplate);
    }

    public List<MailTemplate> findAll(Integer offset, Integer limit, String orderBy, Boolean isDesc) {
        JPAQuery query = new JPAQuery(entityManager).from(mailTemplate);

        if (offset != null)
            query.offset(offset);
        if (limit != null)
            query.limit(limit);

        sortBy(orderBy, isDesc, query);

        return query.list(mailTemplate);

    }

    public Long countAll() {
        JPAQuery query = new JPAQuery(entityManager).from(mailTemplate);
        return query.count();
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
