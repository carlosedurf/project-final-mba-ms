package com.store.user.service.implement;

import com.store.user.domain.Company;
import com.store.user.repository.CompanyRepository;
import com.store.user.service.CompanyService;
import org.springframework.stereotype.Service;

@Service
public class CompanyServiceImplement extends GenericServiceImplement<Company, Long, CompanyRepository> implements CompanyService {
    public CompanyServiceImplement(CompanyRepository repository) {
        super(repository);
    }
}
