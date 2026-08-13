package com.bobocode.services.orders;

import com.bobocode.dto.orders.OrderDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for rendering order history to the console.
 */
@Service
public class OrderConsoleViewService {

    /**
     * Displays the order history for the user.
     *
     * @param orders the list of orders to display
     */
    public void displayOrderHistory(final List<OrderDto> orders) {
        if (orders == null || orders.isEmpty()) {
            System.out.println("You have no order history yet.");
            return;
        }

        System.out.println("================== ORDER HISTORY "
                + "==================");
        for (OrderDto order : orders) {
            System.out.printf("Order ID: %-5d | Date: %s%n",
                    order.getId(), order.getPurchaseDate());
            System.out.println("Items:");

            if (order.getItems() != null) {
                order.getItems().forEach(item ->
                        System.out.printf(
                                "   - Product: %-15s | Price: $%6.2f | "
                                        + "Qty: %-3d%n",
                                item.getProductName(),
                                item.getPriceAtPurchase(),
                                item.getQuantity())
                );
            }
            System.out.println("----------------------------------------"
                    + "-----------");
        }
    }
}
