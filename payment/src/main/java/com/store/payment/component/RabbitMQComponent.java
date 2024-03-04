package com.store.payment.component;

public interface RabbitMQComponent {
    void handleMessage(String message);
}
