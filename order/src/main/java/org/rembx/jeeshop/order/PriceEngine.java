package org.rembx.jeeshop.order;

import org.rembx.jeeshop.order.model.Order;
import org.rembx.jeeshop.order.model.PaymentInfo;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;

/**
 * Created by remi on 07/12/14.
 */
@ApplicationScoped
public interface PriceEngine {
    public void computePrice(Order order);
}
