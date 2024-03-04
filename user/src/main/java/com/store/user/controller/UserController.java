package com.store.user.controller;

import com.store.user.domain.User;
import com.store.user.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
public class UserController extends GenericController<User> {
    public UserController(UserService service) {
        super(service);
    }
}
