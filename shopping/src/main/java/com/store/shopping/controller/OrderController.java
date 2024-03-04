package com.store.shopping.controller;

import com.store.shopping.domain.Order;
import com.store.shopping.service.OrderService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/order")
public class OrderController extends GenericController<Order> {
    public OrderController(OrderService service) {
        super(service);
    }
}
