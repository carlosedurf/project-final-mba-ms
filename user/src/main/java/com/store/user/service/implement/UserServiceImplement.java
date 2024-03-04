package com.store.user.service.implement;

import com.store.user.domain.User;
import com.store.user.repository.UserRepository;
import com.store.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImplement extends GenericServiceImplement<User, Long, UserRepository> implements UserService {
    public UserServiceImplement(UserRepository repository) {
        super(repository);
    }
}
