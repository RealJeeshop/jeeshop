package org.rembx.jeeshop.order.model;

/**
 * Statuses for Order lifecycle
 */
public enum OrderStatus {
    CREATED, APPROVED, VALIDATED, CANCELLED,READY_FOR_SHIPMENT, SHIPPED, DELIVERED, RETURNED
}
