package com.store.shopping.service.implement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.store.shopping.domain.Shopping;
import com.store.shopping.domain.ShoppingItem;
import com.store.shopping.repository.ShoppingItemRepository;
import com.store.shopping.repository.ShoppingRepository;
import com.store.shopping.service.ShoppingService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;

@Service
@Component
public class ShoppingServiceImplement extends GenericServiceImplement<Shopping, Long, ShoppingRepository> implements ShoppingService {

    private final WebClient webClient;

    private final AmqpTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Autowired
    private ShoppingItemRepository shoppingItemRepository;

    public ShoppingServiceImplement(ShoppingRepository repository, WebClient webClient, AmqpTemplate rabbitTemplate) {
        super(repository);
        this.webClient = webClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void save(Shopping shopping) {

        this.webClient.get()
                .uri("/product/" + String.valueOf(shopping.getUser_id()))
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        Shopping shop = repository.save(shopping);
                        for (ShoppingItem item: shop.getShoppingItems()) {
                            ShoppingItem shoppingItem = new ShoppingItem();
                            shoppingItem.setShopping(shop);
                            shoppingItem.setProduct_id(item.getProduct_id());
                            shoppingItemRepository.save(shoppingItem);
                        }
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

    public void sendNotification(Shopping shopping) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            String json = mapper.writeValueAsString(shopping);
            rabbitTemplate.convertAndSend(exchange, routingKey, json);;
        } catch (JsonProcessingException e) {
            System.out.println("Falha: " + e.getMessage());
        }
    }

    @Override
    public void addItem(Long id, ShoppingItem shoppingItem) {
        this.webClient.get()
                .uri("/product/" + String.valueOf(shoppingItem.getProduct_id()))
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        Shopping shopping = this.get(id, "Shopping not found");
                        shoppingItem.setShopping(shopping);
                        shoppingItemRepository.save(shoppingItem);
                        return response.toEntity(String.class);
                    } else if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        System.out.println("Produto não encontrado!");
                        return response.toEntity(String.class);
                    } else {
                        return response.createError();
                    }
                })
                .block();
    }

    @Override
    public void removeItem(Long id) {
        shoppingItemRepository.deleteById(id);
    }

    @Override
    public void sendPayment(Long id) {
        Shopping shopping = this.get(id, "Shopping not found");
        shopping.setStatus(2);
        repository.save(shopping);
        this.sendNotification(shopping);
    }

    @Override
    public void success(Long id) {
        System.out.println("success");
        Shopping shopping = this.get(id, "Shopping not found");
        shopping.setStatus(3);
        repository.save(shopping);
    }
}
