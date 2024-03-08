package com.store.shopping.service;

import com.store.shopping.domain.Shopping;
import com.store.shopping.domain.ShoppingItem;

import java.util.List;

public interface GenericService<T> {

    List<T> getAll();

    T get(Long id, String noSuchElementException);

    void save(T item);

    void update(T item);

    void delete(Long id);

    void addItem(Long id, ShoppingItem shoppingItem);

    void removeItem(Long id);

    void sendPayment(Long id);

    void success(Long id);
}
