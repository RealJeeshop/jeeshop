package org.rembx.jeeshop.order;

/**
 * Created by remi on 21/05/14.
 */
public class Payment {

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
