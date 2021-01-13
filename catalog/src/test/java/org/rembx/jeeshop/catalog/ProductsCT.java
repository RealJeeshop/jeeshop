package org.rembx.jeeshop.catalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rembx.jeeshop.catalog.model.Product;
import org.rembx.jeeshop.catalog.model.SKU;
import org.rembx.jeeshop.catalog.test.CatalogItemCRUDTester;
import org.rembx.jeeshop.catalog.test.TestCatalog;
import org.rembx.jeeshop.rest.WebApplicationException;

import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.rembx.jeeshop.catalog.test.Assertions.assertThatSKUsOf;

public class ProductsCT {

    private Products localService;
    private CatalogItemCRUDTester<Product> tester;

    @BeforeEach
    public void setup() {
        tester = new CatalogItemCRUDTester<>(Product.class);
        localService = new Products(tester.getEntityManager(), new CatalogItemFinder(tester.getEntityManager()), null);
        tester.setService(this.localService);
    }

    @Test
    public void find_withIdOfVisibleProduct_ShouldReturnExpectedProduct() {
        assertThat(localService.find(null, tester.getFixtures().aProductWithSKUs().getId(), null)).isEqualTo(tester.getFixtures().aProductWithSKUs());
        assertThat(localService.find(null, tester.getFixtures().aProductWithSKUs().getId(), null).isVisible()).isTrue();
    }

    @Test
    public void find_withIdOfDisableProduct_ShouldThrowForbiddenException() {
        try {
            localService.find(null, tester.getFixtures().aDisabledProduct().getId(), null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.FORBIDDEN);
        }
    }

    @Test
    public void find_withIdOfExpiredProduct_ShouldThrowForbiddenException() {
        try {
            localService.find(null, tester.getFixtures().anExpiredProduct().getId(), null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.FORBIDDEN);
        }
    }

    @Test
    public void find_withUnknownProductId_ShouldThrowNotFoundException() {
        try {
            localService.find(null, 9999L, null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }


    @Test
    public void findSKUs_shouldReturn404ExWhenProductNotFound() {
        try {
            localService.findChildSKUs(null, 9999L, null);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test
    public void findSKUs_shouldNotReturnExpiredNorDisabledSKUs() {
        List<SKU> skus = localService.findChildSKUs(null, tester.getFixtures().aProductWithSKUs().getId(), null);
        assertThat(skus).isNotEmpty();
        assertThatSKUsOf(skus).areVisibleSKUsOfAProductWithSKUs();
    }

    @Test
    public void findSKUs_shouldReturnEmptyListWhenNoChildProducts() {
        List<SKU> skus = localService.findChildSKUs(null, tester.getFixtures().aProductWithoutSKUs().getId(), null);
        assertThat(skus).isEmpty();
    }


    @Test
    public void findAll_shouldReturnNoneEmptyList() {
        assertThat(localService.findAll(null, null, null, null, null, null)).isNotEmpty();
    }

    @Test
    public void findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        List<Product> categories = localService.findAll(null, 0, 1, null, null, null);
        assertThat(categories).isNotEmpty();
        assertThat(categories).hasSize(1);
    }

    @Test
    public void findAll_withIdSearchParam_shouldReturnResultsWithMatchingId() {
        assertThat(localService.findAll(tester.getFixtures().aProductWithoutSKUs().getId().toString(), null, null, null, null, null)).containsExactly(tester.getFixtures().aProductWithoutSKUs());
    }

    @Test
    public void findAll_withNameSearchParam_shouldReturnResultsWithMatchingName() {
        assertThat(localService.findAll(tester.getFixtures().aProductWithoutSKUs().getName(), null, null, null, null, null)).containsExactly(tester.getFixtures().aProductWithoutSKUs());
    }


    @Test
    public void modifyProduct_ShouldModifyProductAttributesAndPreserveSKUsWhenNotProvided() {

        tester.setAdminUser();

        Product product = new Product(tester.getFixtures().aProductWithSKUs().getId(), "New name");

        tester.test_modify(product);

        assertThat(product.getName()).isEqualTo("New name");
        assertThat(product.getChildSKUs()).containsOnlyElementsOf(product.getChildSKUs());
    }

    @Test
    public void modifyUnknownProduct_ShouldThrowNotFoundException() {

        Product detachedProductToModify = new Product(9999L, null);
        try {
            tester.setAdminUser();
            localService.modify(tester.getSecurityContext(), detachedProductToModify);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }


    @Test
    public void countAll() {
        assertThat(localService.count(null)).isGreaterThan(0);
    }

    @Test
    public void countAll_withUnknownSearchCriteria() {
        assertThat(localService.count("666")).isEqualTo(0);
    }


    @Test
    public void create_shouldPersist_for_admin() {

        tester.setAdminUser();
        Product product = new Product("name", "description", new Date(), new Date(), false, TestCatalog.OWNER);

        Product actualProduct = tester.test_create(product);

        assertThat(actualProduct).isNotNull();
        assertThat(actualProduct.getOwner()).isEqualTo(TestCatalog.OWNER);
    }

    @Test
    public void create_withoutOwner_shouldThrow_BadRequest_for_admin() {

        try {
            tester.setAdminUser();
            Product product = new Product("name");
            tester.test_create(product);
            fail("Should have thrown an exception");

        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.BAD_REQUEST);
        }
    }

    @Test
    public void create_shouldPersist_for_store_admin() {

        tester.setAdminUser();
        Product product = new Product("name", "description", new Date(), new Date(), false, TestCatalog.OWNER);

        Product actualProduct = tester.test_create(product);

        assertThat(actualProduct).isNotNull();
        assertThat(actualProduct.getOwner()).isEqualTo(TestCatalog.OWNER);
    }


    @Test
    public void delete_shouldRemove() {

        tester.setStoreAdminUser();
        Product product = new Product("Test", "", null, null, null, "test@test.com");
        product.setOwner(TestCatalog.OWNER);
        tester.test_delete(product);
        assertThat(tester.getEntityManager().find(Product.class, product.getId())).isNull();
    }

    @Test
    public void delete_NonManagedEntity_shouldThrow_Forbidden() {

        try {
            tester.setStoreAdminUser();
            Product product = new Product("Test", "", null, null, null, "test@test.com");
            tester.test_delete(product);
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.FORBIDDEN);
        }
    }

    @Test
    public void delete_NotExistingEntry_shouldThrowNotFoundEx() {

        try {
            tester.setAdminUser();
            localService.delete(tester.getSecurityContext(), 666L);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

}