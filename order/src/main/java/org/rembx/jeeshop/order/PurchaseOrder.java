package org.rembx.jeeshop.order;

import org.rembx.jeeshop.user.Address;
import org.rembx.jeeshop.user.Customer;

import java.util.List;

/**
 * Created by remi on 21/05/14.
 */
public class PurchaseOrder {

    private enum status{CREATED, FULFILLED, ACCEPTED, REJECTED, CANCELLED, DELIVERED}

    Customer customer;

    List<ShoppingCartItem> shoppingCartItems;

    Address deliveryAddress;

    Address legalAddress;

    Payment payment;

    String expeditionCode;
}
