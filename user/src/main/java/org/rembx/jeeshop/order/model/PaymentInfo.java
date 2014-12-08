package org.rembx.jeeshop.order.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentInfo {

    /**
     * Used to store payment form response for Payment solutions such as SIPS.
     */
    private String paymentFormResponse;

    public PaymentInfo() {
    }

    public PaymentInfo(String paymentFormResponse) {
        this.paymentFormResponse = paymentFormResponse;
    }

    public String getPaymentFormResponse() {
        return paymentFormResponse;
    }

    public void setPaymentFormResponse(String paymentFormResponse) {
        this.paymentFormResponse = paymentFormResponse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PaymentInfo that = (PaymentInfo) o;

        if (paymentFormResponse != null ? !paymentFormResponse.equals(that.paymentFormResponse) : that.paymentFormResponse != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return paymentFormResponse != null ? paymentFormResponse.hashCode() : 0;
    }
}

