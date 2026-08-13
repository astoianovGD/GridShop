package com.bobocode.entities.orders;

import com.bobocode.entities.products.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Represents an individual item within an order.
 */
@Data
@Entity
@Table(name = "order_items")
public class OrderItem {

    /**
     * The unique identifier of the order item.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private long orderItemId;

    /**
     * The order to which this item belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * The product associated with this order item.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * The price of the product at the time of purchase.
     */
    @Column(nullable = false)
    private BigDecimal priceAtPurchase;

    /**
     * The quantity of the product purchased.
     */
    @Column(nullable = false)
    private int quantity;
}
