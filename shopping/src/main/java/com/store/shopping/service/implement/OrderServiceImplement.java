package com.store.shopping.service.implement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.store.shopping.domain.Order;
import com.store.shopping.domain.OrderItem;
import com.store.shopping.repository.OrderItemRepository;
import com.store.shopping.repository.OrderRepository;
import com.store.shopping.service.OrderService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Component
public class OrderServiceImplement extends GenericServiceImplement<Order, Long, OrderRepository> implements OrderService {

    private final WebClient webClient;

    private final AmqpTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public OrderServiceImplement(OrderRepository repository, WebClient webClient, AmqpTemplate rabbitTemplate) {
        super(repository);
        this.webClient = webClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void save(Order order) {
        this.webClient.get()
                .uri("/user/" + String.valueOf(order.getUser_id()))
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        Order ord = repository.save(order);
                        for (OrderItem item: ord.getOrderItems()) {
                            OrderItem orderItem = new OrderItem();
                            orderItem.setOrder(ord);
                            orderItem.setProduct_id(item.getProduct_id());
                            orderItemRepository.save(orderItem);
                        }
                        this.sendNotification(ord);
                        return response.toEntity(String.class);
                    } else if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        System.out.println("Usuário não encontrado!");
                        return response.toEntity(String.class);
                    } else {
                        return response.createError();
                    }
                })
                .block();
    }

    public void sendNotification(Order order) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            String json = mapper.writeValueAsString(order);
            rabbitTemplate.convertAndSend(exchange, routingKey, json);;
        } catch (JsonProcessingException e) {
            System.out.println("Falha: " + e.getMessage());
        }
    }
}
