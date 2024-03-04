package com.store.user.controller;

import com.store.user.domain.Company;
import com.store.user.service.CompanyService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/company")
public class CompanyController extends GenericController<Company> {
    public CompanyController(CompanyService service) {
        super(service);
    }
}
