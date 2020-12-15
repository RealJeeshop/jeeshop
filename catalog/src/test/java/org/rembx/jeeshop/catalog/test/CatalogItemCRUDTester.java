package org.rembx.jeeshop.catalog.test;

import org.apache.http.auth.BasicUserPrincipal;
import org.rembx.jeeshop.catalog.CatalogItems;
import org.rembx.jeeshop.catalog.model.CatalogItem;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.role.JeeshopRoles;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.SecurityContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CatalogItemCRUDTester<T extends CatalogItem> {

    private static EntityManagerFactory entityManagerFactory;
    protected EntityManager entityManager;
    protected SecurityContext securityContext;
    protected TestCatalog testCatalog;
    private Class<T> itemClass;
    protected CatalogItems<T> service;

    public CatalogItems<T> getService() {
        return service;
    }

    public void setService(CatalogItems<T> service) {
        this.service = service;
    }

    public CatalogItemCRUDTester(Class<T> itemClass) {
        this.itemClass = itemClass;
        entityManagerFactory = Persistence.createEntityManagerFactory(CatalogPersistenceUnit.NAME);
    }

    public void setUp() {
        testCatalog = TestCatalog.getInstance();
        entityManager = entityManagerFactory.createEntityManager();
        securityContext = mock(SecurityContext.class);
    }

    public T test_create(T catalogItem) {

        entityManager.getTransaction().begin();
        service.create(securityContext, catalogItem);
        entityManager.getTransaction().commit();

        return refreshCatalogItem(catalogItem);
    }

    public void test_delete(T catalogItem) {

        entityManager.getTransaction().begin();
        entityManager.persist(catalogItem);
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        service.delete(securityContext, catalogItem.getId());
        entityManager.getTransaction().commit();
    }

    public void test_modify(T catalogItem) {
        service.modify(securityContext, catalogItem);
    }

    private T refreshCatalogItem(T store) {
        T actualStore = entityManager.find(itemClass, store.getId());
        entityManager.remove(store);
        return actualStore;
    }

    public void setAdminUser() {
        when(securityContext.getUserPrincipal()).thenReturn(new BasicUserPrincipal("admin@jeeshop.org"));
        when(securityContext.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(true);
    }

    public void setStoreAdminUser() {
        when(securityContext.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(false);
        when(securityContext.isUserInRole(JeeshopRoles.STORE_ADMIN)).thenReturn(true);
        when(securityContext.getUserPrincipal()).thenReturn(new BasicUserPrincipal(TestCatalog.OWNER));
    }

    public void setSAnotherStoreAdminUser() {
        when(securityContext.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(false);
        when(securityContext.isUserInRole(JeeshopRoles.STORE_ADMIN)).thenReturn(true);
        when(securityContext.getUserPrincipal()).thenReturn(new BasicUserPrincipal("test@test.org"));
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public SecurityContext getSecurityContext() {
        return securityContext;
    }

    public TestCatalog getFixtures() {
        return testCatalog;
    }
}
