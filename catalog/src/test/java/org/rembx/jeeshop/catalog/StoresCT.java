package org.rembx.jeeshop.catalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rembx.jeeshop.catalog.model.Catalog;
import org.rembx.jeeshop.catalog.model.Store;
import org.rembx.jeeshop.catalog.test.CatalogItemCRUDTester;
import org.rembx.jeeshop.catalog.test.TestCatalog;
import org.rembx.jeeshop.rest.WebApplicationException;

import javax.ws.rs.core.Response;

import java.time.DayOfWeek;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class StoresCT extends CatalogItemCRUDTester<Store> {

    private Stores localService;

    @Override
    protected Class<Store> getItemClass() {
        return Store.class;
    }

    @Override
    protected CatalogItems<Store> getService() {
        return this.localService;
    }

    @BeforeEach
    public void setup() {
        this.localService = new Stores(entityManager, new CatalogItemFinder(entityManager), null);
    }

    @Test
    public void findAll_shouldReturnNoneEmptyList() {

        assertThat(localService.findAll(null, null, null, null, null, null)).isNotEmpty();
    }

    @Test
    public void find_shouldLoadSchedules() {

        setAdminUser();

        Store store = localService.find(securityContext, 2L, null);
        assertThat(store).isNotNull();
        assertThat(store.getPremisses()).isNotEmpty();
        assertThat(store.getPremisses().get(0).getSchedules()).isNotEmpty();
        assertThat(store.getPremisses().get(0).getSchedules().get(0).getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
    }

    @Test
    public void find_shouldLoadNonVisibleItem_for_admin() {
        setAdminUser();
        Store store = localService.find(securityContext, 1L, null);
        assertThat(store).isNotNull();
    }


    @Test
    public void find_shouldLoadNonVisibleItem_for_store_admin() {
        setStoreAdminUser();
        Store store = localService.find(securityContext, 1L, null);
        assertThat(store).isNotNull();
    }

    @Test
    public void create_shouldSetupOwner_for_admin() {

        setAdminUser();
        Store store = new Store("Superstore");
        store.setOwner(TestCatalog.OWNER);

        Store actualStore = test_create(store);

        assertThat(actualStore).isNotNull();
        assertThat(actualStore.getOwner()).isEqualTo(TestCatalog.OWNER);
    }

    @Test
    public void create_shouldThrowBadRequest_whenOwnerIsNull_for_admin() {

        setAdminUser();
        Store store = new Store("Superstore");

        try {
            test_create(store);
            fail("should have thrown an exception");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.BAD_REQUEST);
        }
    }


    @Test
    public void create_shouldSetupOwner_for_store_admin() {

        setStoreAdminUser();
        Store store = new Store("Superstore");

        Store actualStore = test_create(store);

        assertThat(actualStore).isNotNull();
        assertThat(actualStore.getOwner()).isEqualTo(TestCatalog.OWNER);
    }

    @Test
    public void delete_shouldRemove_for_admin() {
        setAdminUser();

        Store store = new Store("Superstore");
        store.setOwner(TestCatalog.OWNER);
        test_delete(store);

        assertThat(entityManager.find(Store.class, store.getId())).isNull();
    }

    @Test
    public void delete_shouldRemove_for_store_admin() {
        setStoreAdminUser();

        Store store = new Store("Superstore");
        store.setOwner(TestCatalog.OWNER);
        test_delete(store);

        assertThat(entityManager.find(Store.class, store.getId())).isNull();
    }

    @Test
    public void delete_shouldThrowForbidden_for_store_admin() {

        try {
            setStoreAdminUser();
            Store store = new Store("Superstore");
            store.setOwner("test@test.org");
            test_delete(store);
            fail("Should have throw an exception");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.FORBIDDEN);
        }
    }

    @Test
    public void modify_shouldThrowForbidden_for_store_admin() {

        try {
            setStoreAdminUser();
            Store store = new Store(1L, "Superstore");
            store.setOwner("test@test.org");
            test_modify(store);
            fail("Should have throw an exception");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.FORBIDDEN);
        }
    }

    @Test
    public void modify_shouldThrowNotFound_for_store_admin() {

        try {
            setStoreAdminUser();
            Store store = new Store(666L, "Superstore");
            test_modify(store);
            fail("Should have throw an exception");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test
    public void modify_shouldModify_for_store_admin() {

        setStoreAdminUser();
        Store store = new Store(1L, "Superstore 2");
        store.setOwner(TestCatalog.OWNER);
        test_modify(store);

        Store actual = entityManager.find(Store.class, store.getId());
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo("Superstore 2");
    }

    @Test
    public void modify_shouldModify_for_admin() {

        setAdminUser();
        Store store = new Store(1L, "Superstore 2");
        store.setOwner(TestCatalog.OWNER);
        test_modify(store);

        Store actual = entityManager.find(Store.class, store.getId());
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo("Superstore 2");
    }

    @Test
    public void findCatalog_should_retrieve_catalogs() {

        List<Catalog> catalogs = localService.findCatalogs(securityContext, 2L, null);
        assertThat(catalogs).isNotEmpty();
    }
}
