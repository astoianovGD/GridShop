package com.bobocode.entities.orders;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
    private Long orderItemId;

    /**
     * The order to which this item belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull
    private Order order;

    /**
     * The product ID associated with this order item.
     */
    @Column(name = "product_id", nullable = false)
    @NotNull
    private Long productId;

    /**
     * Name of the product at purchase.
     */
    @Column(name = "product_name")
    private String productName;

    /**
     * The price of the product at the time of purchase.
     */
    @Column(name = "price_at_purchase", nullable = false)
    @NotNull
    @Min(0)
    private BigDecimal priceAtPurchase;

    /**
     * The quantity of the product purchased.
     */
    @Column(nullable = false)
    @NotNull
    @Min(1)
    private Integer quantity;
}
