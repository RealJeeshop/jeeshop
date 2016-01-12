package org.rembx.jeeshop.store;

import org.rembx.jeeshop.order.PaymentTransactionEngine;
import org.rembx.jeeshop.order.model.Order;

// TODO
public class SIPSPaymentEngine /*implements PaymentTransactionEngine*/ {

    /*private final static Logger LOG = LoggerFactory.getLogger(SIPSPaymentEngine.class);

    static final String OPENSHIFT_DATA_DIR = "OPENSHIFT_DATA_DIR";
    public static final String MERCHANT_ID = "1234"; // Your MerchantId here
    public static final String MERCHANT_COUNTRY = "fr";
*/
    //@Override
    public void processPayment(Order order) {
        /*LOG.info("Start payment process Order : " + order);

        try {

            SIPSApiWeb api = new SIPSApiWeb(Paths.get(System.getenv(OPENSHIFT_DATA_DIR)).resolve("sips/payment/param/pathfile").toString());

            SIPSDataObject call = new SIPSCallParm();

            call.setValue("merchant_id", MERCHANT_ID);
            call.setValue("merchant_country", MERCHANT_COUNTRY);
            call.setValue("advert", "logo_mercanet.png");
            call.setValue("amount", Integer.toString((int)(order.getPrice()*100)));
            call.setValue("customer_email", order.getUser().getLogin());
            call.setValue("currency_code", "978");
            call.setValue("customer_id",order.getUser().getId().toString());
            call.setValue("caddie", order.getId().toString());

            String response = api.sipsPaymentCallFunc(call);

            LOG.info("SIPS response : " + response);

            order.setPaymentInfo(new PaymentInfo(response));

        } catch (FileNotFoundException e) {
            throw new IllegalStateException("SIPS Configuration error. Cannot get pathfile", e);
        } catch (SIPSException e) {
            throw new IllegalStateException("SIPS Exception caught during SIPS request building", e);
        } catch (Exception e) {
            throw new IllegalStateException("Exception during SIPS request call", e);
        }
*/
    }
}
