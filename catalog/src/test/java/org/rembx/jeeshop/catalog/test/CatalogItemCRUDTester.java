package org.rembx.jeeshop.catalog.test;

import org.apache.http.auth.BasicUserPrincipal;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.rembx.jeeshop.catalog.CatalogItems;
import org.rembx.jeeshop.catalog.model.CatalogItem;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.role.JeeshopRoles;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.SecurityContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class CatalogItemCRUDTester<T extends CatalogItem> {

    private static EntityManagerFactory entityManagerFactory;
    protected EntityManager entityManager;
    protected SecurityContext securityContext;
    protected TestCatalog testCatalog;

    protected abstract Class<T> getItemClass();
    protected abstract CatalogItems<T> getService();

    @BeforeAll
    public static void beforeClass() {
        entityManagerFactory = Persistence.createEntityManagerFactory(CatalogPersistenceUnit.NAME);
    }

    @BeforeEach
    public void setUp() {
        testCatalog = TestCatalog.getInstance();
        entityManager = entityManagerFactory.createEntityManager();
        securityContext = mock(SecurityContext.class);
    }

    protected T test_create(T catalogItem) {

        entityManager.getTransaction().begin();
        getService().create(securityContext, catalogItem);
        entityManager.getTransaction().commit();

        return refreshCatalogItem(catalogItem);
    }

    protected void test_delete(T catalogItem) {

        entityManager.getTransaction().begin();
        entityManager.persist(catalogItem);
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        getService().delete(securityContext, catalogItem.getId());
        entityManager.getTransaction().commit();
    }

    protected void test_modify(T catalogItem) {
        getService().modify(securityContext, catalogItem);
    }

    private T refreshCatalogItem(T store) {
        T actualStore = entityManager.find(getItemClass(), store.getId());
        entityManager.remove(store);
        return actualStore;
    }

    protected void setAdminUser() {
        when(securityContext.getUserPrincipal()).thenReturn(new BasicUserPrincipal("admin@jeeshop.org"));
        when(securityContext.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(true);
    }

    protected void setStoreAdminUser() {
        when(securityContext.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(false);
        when(securityContext.isUserInRole(JeeshopRoles.STORE_ADMIN)).thenReturn(true);
        when(securityContext.getUserPrincipal()).thenReturn(new BasicUserPrincipal(TestCatalog.OWNER));
    }
}
