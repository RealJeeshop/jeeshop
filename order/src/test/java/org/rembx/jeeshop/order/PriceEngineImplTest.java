package org.rembx.jeeshop.order;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.rembx.jeeshop.catalog.DiscountFinder;
import org.rembx.jeeshop.catalog.model.SKU;
import org.rembx.jeeshop.order.model.Order;
import org.rembx.jeeshop.order.model.OrderItem;

import javax.persistence.EntityManager;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;
import static org.mockito.Mockito.*;

public class PriceEngineImplTest { // TODO complete discounts application test

    private EntityManager entityManager;
    private OrderConfiguration orderConfiguration;
    private OrderFinder orderFinder;
    private DiscountFinder discountFinder;

    private PriceEngineImpl orderPriceEngine;


    @Before
    public void setup(){
        entityManager =  mock(EntityManager.class);
        orderConfiguration =  mock(OrderConfiguration.class);
        orderFinder = mock(OrderFinder.class);
        discountFinder = mock(DiscountFinder.class);

        orderPriceEngine = new PriceEngineImpl(entityManager,orderConfiguration, orderFinder, discountFinder);
    }


    @Test
    public void computePrice_ShouldAddPriceOfEachOrderItemsMultipliedByQuantityWithFixedDeliveryFee() {
        SKU sku = new SKU();
        sku.setPrice(10.0);

        SKU sku2 = new SKU();
        sku2.setPrice(20.0);

        when(entityManager.find(SKU.class, 1L)).thenReturn(sku);
        when(entityManager.find(SKU.class, 2L)).thenReturn(sku2);
        when(orderConfiguration.getFixedDeliveryFee()).thenReturn(11.0);

        Order order = new Order();
        order.setItems(Sets.newHashSet(new OrderItem(1L,1L, 1), new OrderItem(2L,2L, 2)));

        orderPriceEngine.computePrice(order);

        verify(entityManager).find(SKU.class, 1L);
        verify(entityManager).find(SKU.class, 2L);

        verify(orderConfiguration).getFixedDeliveryFee();


        assertThat(order.getPrice()).isEqualTo(61.0);
    }


    @Test
    public void computePrice_ShouldAddPriceOfEachOrderItemsMultipliedByQuantity() {
        SKU sku = new SKU();
        sku.setPrice(10.0);

        SKU sku2 = new SKU();
        sku2.setPrice(20.0);

        when(entityManager.find(SKU.class, 1L)).thenReturn(sku);
        when(entityManager.find(SKU.class, 2L)).thenReturn(sku2);
        when(orderConfiguration.getFixedDeliveryFee()).thenReturn(null);

        Order order = new Order();
        order.setItems(Sets.newHashSet(new OrderItem(1L,1L, 1), new OrderItem(2L,2L, 2)));

        orderPriceEngine.computePrice(order);

        verify(entityManager).find(SKU.class, 1L);
        verify(entityManager).find(SKU.class, 2L);

        verify(orderConfiguration).getFixedDeliveryFee();

        assertThat(order.getPrice()).isEqualTo(50.0);
        for (OrderItem item : order.getItems()){
            if (item.getSkuId().equals(1L))
                assertThat(item.getPrice()).isEqualTo(10.0);
            else
                assertThat(item.getPrice()).isEqualTo(20.0);
        }
    }

    @Test
    public void computePrice_ShouldThrowEx_whenOrderHasNoItems() {

        Order order = new Order();

        try {
            orderPriceEngine.computePrice(order);
            fail("Should have thrown ex");
        }catch (IllegalStateException e){
        }
    }
}