package org.rembx.jeeshop.store;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.ws.rs.Path;


@Path("sipsautoresponse")
@Transactional
@ApplicationScoped
// TODO
public class SIPSAutoResponse {

    /*private final static Logger LOG = LoggerFactory.getLogger(SIPSAutoResponse.class);

    static final String OPENSHIFT_DATA_DIR = "OPENSHIFT_DATA_DIR";

    @PersistenceContext(unitName = UserPersistenceUnit.NAME)
    private EntityManager userEntityManager;

    @PersistenceContext(unitName = CatalogPersistenceUnit.NAME)
    private EntityManager catalogEntityManager;

    @Inject
    private OrderFinder orderFinder;

    @Inject
    private MailTemplateFinder mailTemplateFinder;

    @Inject
    private Mailer mailer;

    @POST
    @PermitAll
    public Response processAutoResponse(@FormParam("DATA") String cypheredtxt, @Context HttpServletRequest servletRequest) {
        LOG.info("Processing SIPS Auto Response... ");

        SIPSApiWeb api;

        Response response = Response.seeOther(URI.create("http://"+servletRequest.getServerName())).build();

        try {
            api = new SIPSApiWeb(Paths.get(System.getenv(OPENSHIFT_DATA_DIR)).resolve("sips/payment/param/pathfile").toString());

            // Décryptage de la réponse
            SIPSDataObject resp = api.sipsPaymentResponseFunc(cypheredtxt);

            String transactionId = resp.getValue("transaction_id");
            String customerId = resp.getValue("customer_id");
            String orderId = resp.getValue("caddie");

            if ( ! resp.getValue("response_code").equals("00")){
                LOG.info("Transaction has not been accepted for order ID: {}, transactionId: {} , userId: {}",orderId,transactionId, customerId);
                return response;
            }

            Order order = userEntityManager.find(Order.class, Long.parseLong(orderId));

            if ((order == null) || (order.getUser().getId() != Long.parseLong(customerId))){
                throw new IllegalStateException("Cannot find order with id "+ orderId+ ", transactionId: "+transactionId + ", and user "+ customerId);
            }

            if (order.getStatus().equals(OrderStatus.PAYMENT_VALIDATED)){
                LOG.debug("Payment of order with id {} has already been validated. Nothing to do ",orderId);
                return response;
            }

            updateOrderWithPaymentInfo(resp, order);

            // decrement sku quantities
            for (OrderItem orderItem : order.getItems()){
                SKU sku = catalogEntityManager.find(SKU.class, (orderItem).getSkuId());
                sku.setQuantity(sku.getQuantity() - (orderItem).getQuantity());
            }

            order.computeOrderReference();

            orderFinder.enhanceOrder(order);

            sendOrderConfirmationMail(order);


        }catch (Exception e){
            throw new IllegalStateException("Caught exception while processing SIPS response ",e);
        }

        return response;

    }

    private void updateOrderWithPaymentInfo(SIPSDataObject resp, Order order) throws ParseException, SIPSException {
        order.setStatus(OrderStatus.PAYMENT_VALIDATED);
        order.setPaymentDate(new SimpleDateFormat("yyyyMMdd").parse(resp.getValue("payment_date")));
        order.setTransactionId(resp.getValue("transaction_id"));
    }

    private void sendOrderConfirmationMail(Order order) {

        User user = order.getUser();

        MailTemplate mailTemplate = mailTemplateFinder.findByNameAndLocale(orderValidated.name(), user.getPreferredLocale());

        try {
            Template mailContentTpl = new Template(orderValidated.name(),mailTemplate.getContent(),new Configuration(Configuration.VERSION_2_3_21));
            final StringWriter mailBody = new StringWriter();
            mailContentTpl.process(order, mailBody);
            mailer.sendMail(mailTemplate.getSubject(), user.getLogin(), mailBody.toString());
        }catch (Exception e){
            LOG.error("Unable to send mail "+orderValidated+" to user "+user.getLogin(), e);
        }
    }*/
}
