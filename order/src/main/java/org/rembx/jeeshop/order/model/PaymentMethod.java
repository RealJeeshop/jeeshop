package org.rembx.jeeshop.order.model;

/**
 * Created by remi on 21/05/14.
 */
public class PaymentMethod {

    private enum type {
        VISA,
        MASTER_CARD,
        AMERICAN_EXPRESS
    }

    private String creditCardNumber;

    private String creditCardExpDate;

    private String owner;

    private Integer CVV;
}
