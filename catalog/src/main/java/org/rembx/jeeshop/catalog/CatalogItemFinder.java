package org.rembx.jeeshop.catalog;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.path.EntityPathBase;
import org.rembx.jeeshop.catalog.model.CatalogItem;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.QCatalogItem;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

/**
 * Utility class for common finders on CatalogItem entities
 */
public class CatalogItemFinder {
    @PersistenceContext(unitName = CatalogPersistenceUnit.NAME)
    private EntityManager entityManager;

    public CatalogItemFinder() {
    }

    public CatalogItemFinder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public <T extends CatalogItem> List<T> findVisibleCatalogItems(EntityPathBase<T> entityPathBase, List<T> items, String locale) {
        QCatalogItem qCatalogItem = new QCatalogItem(entityPathBase);
        Date now = new Date();
        List<T> results = new JPAQuery(entityManager)
                .from(qCatalogItem).where(
                        qCatalogItem.disabled.isFalse(),
                        qCatalogItem.endDate.after(now),
                        qCatalogItem.startDate.before(now),
                        qCatalogItem.in(items)
                )
                .list(entityPathBase);

        results.forEach((catalogItem) -> catalogItem.setLocalizedPresentation(locale));

        return results;

    }

}
