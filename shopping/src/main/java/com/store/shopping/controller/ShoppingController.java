package com.store.shopping.controller;

import com.store.shopping.domain.Shopping;
import com.store.shopping.service.ShoppingService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/shopping")
public class ShoppingController extends GenericController<Shopping> {
    public ShoppingController(ShoppingService service) {
        super(service);
    }
}
