package com.store.shopping.controller;

import com.store.shopping.domain.Shopping;
import com.store.shopping.domain.ShoppingItem;
import com.store.shopping.service.GenericService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
public class GenericController<T> {
    protected final GenericService<T> service;

    @GetMapping
    public ResponseEntity<List<T>> getAll() {
        List<T> items = service.getAll();
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<T> get(@PathVariable Long id, String noSuchElementException) {
        T item = service.get(id, noSuchElementException);
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Void> create(@RequestBody T item) {
        service.save(item);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody T item) {
        service.update(item);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/item")
    public ResponseEntity<Void> addItem(@PathVariable Long id, @RequestBody ShoppingItem item) {
        service.addItem(id, item);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/item/{id}")
    public ResponseEntity<Void> addItem(@PathVariable Long id) {
        service.removeItem(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-payment/{id}")
    public ResponseEntity<Void> sendPayment(@PathVariable Long id) {
        service.sendPayment(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/success/{id}")
    public ResponseEntity<Void> success(@PathVariable Long id) {
        service.success(id);
        return ResponseEntity.ok().build();
    }
}
