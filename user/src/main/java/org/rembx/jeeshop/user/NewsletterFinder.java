package org.rembx.jeeshop.user;

import com.mysema.query.jpa.impl.JPAQuery;
import org.rembx.jeeshop.user.model.Newsletter;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.rembx.jeeshop.user.model.QNewsletter.newsletter;

/**
 * Newsletter finder utility
 */
public class NewsletterFinder {

    @PersistenceContext(unitName = UserPersistenceUnit.NAME)
    private EntityManager entityManager;

    public NewsletterFinder() {
    }

    public NewsletterFinder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Newsletter findByName(String name) {
        return new JPAQuery(entityManager)
                .from(newsletter).where(
                        newsletter.name.eq(name))
                .singleResult(newsletter);
    }

    public List<Newsletter> findAll(Integer offset, Integer limit) {
        JPAQuery query = new JPAQuery(entityManager).from(newsletter);

        if (offset != null)
            query.offset(offset);
        if (limit != null)
            query.limit(limit);

        return query.list(newsletter);

    }

    public Long countAll() {
        JPAQuery query = new JPAQuery(entityManager).from(newsletter);
        return query.count();
    }
}
