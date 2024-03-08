package com.store.shopping.service.implement;

import com.store.shopping.domain.Shopping;
import com.store.shopping.domain.ShoppingItem;
import com.store.shopping.service.GenericService;
import jakarta.persistence.criteria.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.NoSuchElementException;

public class GenericServiceImplement<T, ID, R extends JpaRepository<T, ID>> implements GenericService<T> {
    protected final R repository;

    public GenericServiceImplement(R repository){
        this.repository = repository;
    }

    @Override
    public List<T> getAll() {
        return repository.findAll();
    }

    @Override
    public T get(Long id, String noSuchElementException) {
        return repository.findById((ID) id).orElseThrow(() -> new NoSuchElementException(noSuchElementException + id));
    }

    @Override
    public void save(T item) {
        repository.save(item);
    }

    @Override
    public void update(T item) {
        repository.save(item);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById((ID) id);
    }

    @Override
    public void addItem(Long id, ShoppingItem item) {
        System.out.println("addItem");
    }

    @Override
    public void removeItem(Long id) {
        System.out.println("removeItem");
    }

    @Override
    public void sendPayment(Long id) {
        System.out.println("sendPayment");
    }

    @Override
    public void success(Long id) {
        System.out.println("success");
    }
}
