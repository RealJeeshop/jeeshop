package org.rembx.jeeshop.order.model;

import org.rembx.jeeshop.user.model.Address;
import org.rembx.jeeshop.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Collection;
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

    @ManyToOne (optional = false)
    @NotNull
    User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    @XmlTransient
    Collection<OrderItem> items;

    @OneToOne(cascade = CascadeType.ALL)
    Address deliveryAddress;

    @OneToOne(cascade = CascadeType.ALL)
    Address billingAddress;

    //PaymentMethod payment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private OrderStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date creationDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

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

    public Collection<OrderItem> getItems() {
        return items;
    }

    public void setItems(Collection<OrderItem> items) {
        this.items = items;
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



    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (id != null ? !id.equals(order.id) : order.id != null) return false;
        if (status != order.status) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}
