package org.rembx.jeeshop.admin;

import org.rembx.jeeshop.order.PaymentTransactionEngine;
import org.rembx.jeeshop.order.model.Order;
import org.rembx.jeeshop.order.model.PaymentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by remi on 08/12/14.
 */
public class PaymentTransactionEngineFakeImpl implements PaymentTransactionEngine {
    private final static Logger LOG = LoggerFactory.getLogger(PaymentTransactionEngineFakeImpl.class);

    @Override
    public void processPayment(Order order) {
        LOG.info("PaymentTransactionEngine is not configured");
    }
}
