package org.rembx.jeeshop.order.model;

import org.rembx.jeeshop.user.model.Address;
import org.rembx.jeeshop.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * Order entity
 */
@Entity
@Table(name = "Orders")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Order {

    public static final String ORDER_REF_SEPARATOR = "-";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", fetch = FetchType.EAGER)
    Set<OrderItem> items;

    @ElementCollection (fetch = FetchType.EAGER)
            @CollectionTable(name = "OrderDiscount", joinColumns = @JoinColumn(name = "order_id"))
    Set<OrderDiscount> orderDiscounts;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Address deliveryAddress;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Address billingAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private OrderStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date creationDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    Double price;

    @Column(length = 50)
    private String transactionId;

    @Column(length = 50)
    private String parcelTrackingKey;

    @Temporal(TemporalType.TIMESTAMP)
    private Date deliveryDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;

    // Transient properties
    @Transient
    PaymentInfo paymentInfo; // Used for payment systems such as SIPS

    @Transient
    private Double deliveryFee;

    @Transient
    private Double vat;

    @Transient
    private String reference;

    public Order() {
    }

    public Order( Set<OrderItem> items, Address deliveryAddress, Address billingAddress) {
        this.items = items;
        this.deliveryAddress = deliveryAddress;
        this.billingAddress = billingAddress;
    }

    public Order(User user, Set<OrderItem> items, Address deliveryAddress, Address billingAddress, OrderStatus status) {
        this.user = user;
        this.items = items;
        this.deliveryAddress = deliveryAddress;
        this.billingAddress = billingAddress;
        this.status = status;
    }

    @PrePersist
    public void prePersist() {
        final Date date = new Date();
        this.creationDate = date;
        this.updateDate = date;
    }

    @PreUpdate
    public void preUpdate(){
        this.updateDate = new Date();
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    public void computeOrderReference(){
        SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy");
        reference = sdf.format(creationDate)+ORDER_REF_SEPARATOR+id;
        if (transactionId != null){
            reference += ORDER_REF_SEPARATOR +transactionId;
        }
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getParcelTrackingKey() {
        return parcelTrackingKey;
    }

    public void setParcelTrackingKey(String parcelTrackingKey) {
        this.parcelTrackingKey = parcelTrackingKey;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Set<OrderItem> getItems() {
        return items;
    }

    public void setItems(Set<OrderItem> items) {
        this.items = items;
    }

    public Set<OrderDiscount> getOrderDiscounts() {
        return orderDiscounts;
    }

    public void setOrderDiscounts(Set<OrderDiscount> orderDiscounts) {
        this.orderDiscounts = orderDiscounts;
    }

    public Double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(Double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Double getVat() {
        return vat;
    }

    public void setVat(Double vat) {
        this.vat = vat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (id != null ? !id.equals(order.id) : order.id != null) return false;
        if (parcelTrackingKey != null ? !parcelTrackingKey.equals(order.parcelTrackingKey) : order.parcelTrackingKey != null)
            return false;
        if (price != null ? !price.equals(order.price) : order.price != null) return false;
        if (reference != null ? !reference.equals(order.reference) : order.reference != null) return false;
        if (status != order.status) return false;
        if (transactionId != null ? !transactionId.equals(order.transactionId) : order.transactionId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (transactionId != null ? transactionId.hashCode() : 0);
        result = 31 * result + (parcelTrackingKey != null ? parcelTrackingKey.hashCode() : 0);
        result = 31 * result + (reference != null ? reference.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", status=" + status +
                ", price=" + price +
                ", transactionId='" + transactionId + '\'' +
                ", parcelTrackingKey='" + parcelTrackingKey + '\'' +
                ", deliveryFee=" + deliveryFee +
                ", reference='" + reference + '\'' +
                '}';
    }
}
