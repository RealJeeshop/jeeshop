package org.rembx.jeeshop.order.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.net.URI;

/**
 * Order item.
 * Represents a stock unit price and its ordered quantity
 */

@Entity
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @XmlTransient
    private Order order;

    @NotNull
    @Column(name = "product_id")
    private Long productId;

    @NotNull
    @Column(name = "sku_id")
    private Long skuId;

    @NotNull
    @Column(nullable = false)
    private Integer quantity;

    private Double price;

    // Transient computed properties
    @Transient
    private String displayName;

    @Transient
    private String skuReference;

    @Transient
    private String presentationImageURI;

    public OrderItem() {
    }

    public OrderItem(Long skuId, Long productId, Integer quantity) {
        this.skuId = skuId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPresentationImageURI() {
        return presentationImageURI;
    }

    public void setPresentationImageURI(String presentationImageURI) {
        this.presentationImageURI = presentationImageURI;
    }

    public String getSkuReference() {
        return skuReference;
    }

    public void setSkuReference(String skuReference) {
        this.skuReference = skuReference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderItem orderItem = (OrderItem) o;

        if (displayName != null ? !displayName.equals(orderItem.displayName) : orderItem.displayName != null)
            return false;
        if (id != null ? !id.equals(orderItem.id) : orderItem.id != null) return false;
        if (presentationImageURI != null ? !presentationImageURI.equals(orderItem.presentationImageURI) : orderItem.presentationImageURI != null)
            return false;
        if (price != null ? !price.equals(orderItem.price) : orderItem.price != null) return false;
        if (productId != null ? !productId.equals(orderItem.productId) : orderItem.productId != null) return false;
        if (quantity != null ? !quantity.equals(orderItem.quantity) : orderItem.quantity != null) return false;
        if (skuId != null ? !skuId.equals(orderItem.skuId) : orderItem.skuId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (productId != null ? productId.hashCode() : 0);
        result = 31 * result + (skuId != null ? skuId.hashCode() : 0);
        result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (presentationImageURI != null ? presentationImageURI.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        return result;
    }
}
