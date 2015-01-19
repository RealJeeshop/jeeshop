package org.rembx.jeeshop.order.model;

import org.rembx.jeeshop.user.model.Address;
import org.rembx.jeeshop.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

/**
 * Order entity
 */
@Entity
@Table(name = "Orders")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", fetch = FetchType.EAGER)
    List<OrderItem> items;

    @OneToOne(cascade = CascadeType.ALL)
    Address deliveryAddress;

    @OneToOne(cascade = CascadeType.ALL)
    Address billingAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private OrderStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date creationDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    @Transient
    PaymentInfo paymentInfo; // Used for payment systems such as SIPS

    @Transient
    Double computedPrice;

    @Column(length = 50)
    private String transactionId;

    @Column(length = 50)
    private String parcelTrackingKey;

    @Temporal(TemporalType.TIMESTAMP)
    private Date deliveryDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;

    public Order() {
    }

    public Order( List<OrderItem> items, Address deliveryAddress, Address billingAddress) {
        this.items = items;
        this.deliveryAddress = deliveryAddress;
        this.billingAddress = billingAddress;
    }

    public Order(User user, List<OrderItem> items, Address deliveryAddress, Address billingAddress, OrderStatus status) {
        this.user = user;
        this.items = items;
        this.deliveryAddress = deliveryAddress;
        this.billingAddress = billingAddress;
        this.status = status;
    }

    @PrePersist
    public void prePersist() {
        this.creationDate = new Date();
    }

    @PreUpdate
    public void preUpdate(){
        this.updateDate = new Date();
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

    public Double getComputedPrice() {
        return computedPrice;
    }

    public void setComputedPrice(Double computedPrice) {
        this.computedPrice = computedPrice;
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

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (id != null ? !id.equals(order.id) : order.id != null) return false;
        if (status != order.status) return false;
        if (transactionId != null ? !transactionId.equals(order.transactionId) : order.transactionId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (transactionId != null ? transactionId.hashCode() : 0);
        return result;
    }
}
