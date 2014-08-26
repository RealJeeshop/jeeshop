package org.rembx.jeeshop.catalog;


import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rembx.jeeshop.catalog.model.CatalogItem;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Presentation;
import org.rembx.jeeshop.catalog.test.TestCatalog;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.util.HashMap;
import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;

public class PresentationResourceIT {

    private PresentationResource service;

    private TestCatalog testCatalog;
    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @BeforeClass
    public static void beforeClass() {
        entityManagerFactory = Persistence.createEntityManagerFactory(CatalogPersistenceUnit.NAME);
    }

    @Before
    public void setup() {
        testCatalog = TestCatalog.getInstance();
        entityManager = entityManagerFactory.createEntityManager();
    }

    @Test
    public void find_shouldReturnPresentation() {
        Presentation presentation = new Presentation("en_GB", "presentation1", "short description", "long description");
        service = new PresentationResource(null, null, null, presentation);

        assertThat(service.find()).isEqualTo(presentation);
    }

    @Test
    public void createLocalizedPresentation_shouldPersistGivenPresentationAndLinkItToParentCatalogItem() {
        CatalogItem parentCatalogItem = testCatalog.aCategoryWithoutPresentation();

        Presentation presentation = new Presentation(null, "presentation test", "testShortDesc", "testLongDesc");
        service = new PresentationResource(entityManager, parentCatalogItem, "fr_FR", null);

        entityManager.getTransaction().begin(); // wrap call in transaction as method is transactional
        service.createLocalizedPresentation(presentation);
        entityManager.getTransaction().commit();

        assertThat(presentation.getId()).isNotNull();
        assertThat(entityManager.find(Presentation.class, presentation.getId())).isNotNull();
        assertThat(parentCatalogItem.getPresentationByLocale().get("fr_FR")).isNotNull();
        assertThat(parentCatalogItem.getPresentationByLocale().get("fr_FR").getLocale()).isEqualTo("fr_FR");

        // cleanup
        removePersistedTestData(parentCatalogItem, presentation);
    }

    @Test
    public void modifyLocalizedPresentation_shouldUpdateGivenPresentation() {

        Presentation presentation = createTestPresentation();

        service = new PresentationResource(entityManager, null, "fr_FR", presentation);

        UUID uuid = UUID.randomUUID();
        presentation.setShortDescription(uuid.toString());

        entityManager.getTransaction().begin(); // wrap call in transaction as method is transactional
        service.modifyLocalizedPresentation(presentation);
        entityManager.getTransaction().commit();

        assertThat(entityManager.find(Presentation.class, presentation.getId()).getShortDescription()).isEqualTo(uuid.toString());

        // cleanup
        removePersistedTestData(null, presentation);
    }

    @Test
    public void deleteGivenPresentation_shouldRemoveIt() {

        CatalogItem parentCatalogItem = testCatalog.aCategoryWithoutPresentation();

        Presentation presentation = createTestPresentation();
        parentCatalogItem.getPresentationByLocale().put("fr_FR",presentation);

        service = new PresentationResource(entityManager, parentCatalogItem, "fr_FR", presentation);

        entityManager.getTransaction().begin(); // wrap call in transaction as method is transactional
        service.delete();
        entityManager.getTransaction().commit();

        assertThat(entityManager.find(Presentation.class, presentation.getId())).isNull();
        assertThat(parentCatalogItem.getPresentationByLocale().get("fr_FR")).isNull();
    }


    private Presentation createTestPresentation() {
        entityManager.getTransaction().begin();
        Presentation presentation = new Presentation("fr_FR", "presentation test", "testShortDesc", "testLongDesc");
        entityManager.persist(presentation);
        entityManager.getTransaction().commit();
        return presentation;
    }

    private void removePersistedTestData(CatalogItem parentCatalogItem, Presentation presentation) {
        entityManager.getTransaction().begin();
        entityManager.remove(presentation);
        if (parentCatalogItem != null)
            parentCatalogItem.setPresentationByLocale(new HashMap<>());
        entityManager.getTransaction().commit();
    }

}