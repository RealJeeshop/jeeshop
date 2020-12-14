package org.rembx.jeeshop.catalog;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Premises;
import org.rembx.jeeshop.catalog.model.Store;
import org.rembx.jeeshop.catalog.test.TestCatalog;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.time.DayOfWeek;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StoresCT {

    private Stores service;

    private TestCatalog testCatalog;
    private static EntityManagerFactory entityManagerFactory;
    EntityManager entityManager;

    @BeforeAll
    public static void beforeClass() {
        entityManagerFactory = Persistence.createEntityManagerFactory(CatalogPersistenceUnit.NAME);
    }

    @BeforeEach
    public void setup() {
        testCatalog = TestCatalog.getInstance();
        entityManager = entityManagerFactory.createEntityManager();
        service = new Stores(entityManager, new CatalogItemFinder(entityManager), null);
    }

    @Test
    public void findAll_shouldReturnNoneEmptyList() {
        assertThat(service.findAll(null, null, null, null, null, null)).isNotEmpty();
    }

    @Test
    public void findAll_shouldLoadSchedules() {
        List<Store> all = service.findAll(null, null, null, null, null, null);
        assertThat(all).isNotEmpty();

        List<Premises> premisses = all.get(0).getPremisses();
        assertThat(premisses).isNotEmpty();
        assertThat(premisses.get(0).getSchedules()).isNotEmpty();
        assertThat(premisses.get(0).getSchedules().get(0).getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
    }
}
