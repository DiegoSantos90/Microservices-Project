package com.br.diego.orderservice.controller;

import com.br.diego.orderservice.client.InventoryClient;
import com.br.diego.orderservice.dto.OrderDto;
import com.br.diego.orderservice.model.Order;
import com.br.diego.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/order")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    @PostMapping
    public String placeOrder(@RequestBody OrderDto orderDto){
        boolean allProductsInStock = orderDto.getOrderLineItemsList().stream()
                .allMatch(orderLineItems -> inventoryClient.checkStock(orderLineItems.getSkuCode()));

        if(allProductsInStock) {
            Order order = new Order();
            order.setOrderLineItems(orderDto.getOrderLineItemsList());
            order.setOrderNumber(UUID.randomUUID().toString());

            orderRepository.save(order);

            return "Order Place Successfully";
        } else {
            return  "Order Failed. One of the products in the order is not in stock";
        }
    }
}
