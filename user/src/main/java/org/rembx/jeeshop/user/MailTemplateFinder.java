package org.rembx.jeeshop.user;

import com.mysema.query.jpa.impl.JPAQuery;
import org.rembx.jeeshop.user.model.MailTemplate;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.rembx.jeeshop.user.model.QMailTemplate.mailTemplate;

/**
 * Newsletter finder utility
 */
public class MailTemplateFinder {

    public final static String DEFAULT_LOCALE = "en_GB";

    @PersistenceContext(unitName = UserPersistenceUnit.NAME)
    private EntityManager entityManager;

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
                .and(mailTemplate.locale.eq(locale)))
                .singleResult(mailTemplate);
    }

    public MailTemplate findByName(String name) {

        return new JPAQuery(entityManager)
                .from(mailTemplate).where(
                        mailTemplate.name.eq(name))
                .singleResult(mailTemplate);
    }

    public List<MailTemplate> findAll(Integer offset, Integer limit) {
        JPAQuery query = new JPAQuery(entityManager).from(mailTemplate);

        if (offset != null)
            query.offset(offset);
        if (limit != null)
            query.limit(limit);

        return query.list(mailTemplate);

    }

    public Long countAll() {
        JPAQuery query = new JPAQuery(entityManager).from(mailTemplate);
        return query.count();
    }
}
