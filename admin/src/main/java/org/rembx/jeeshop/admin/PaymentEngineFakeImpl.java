package org.rembx.jeeshop.admin;

import org.rembx.jeeshop.order.PaymentEngine;
import org.rembx.jeeshop.order.model.Order;
import org.rembx.jeeshop.order.model.PaymentInfo;

/**
 * Created by remi on 08/12/14.
 */
public class PaymentEngineFakeImpl implements PaymentEngine {
    @Override
    public PaymentInfo execute(Order order) {
        return null;
    }
}
