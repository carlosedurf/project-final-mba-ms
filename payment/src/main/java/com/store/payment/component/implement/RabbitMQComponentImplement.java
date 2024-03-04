package com.store.payment.component.implement;

import com.store.payment.component.RabbitMQComponent;
import com.store.payment.service.implement.EmailServiceImplement;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class RabbitMQComponentImplement implements RabbitMQComponent {
    @Value("${rabbitmq.queue.name}")
    private String queue;

    @Autowired
    private EmailServiceImplement emailServiceImplement;

    private final WebClient webClient;

    public RabbitMQComponentImplement(WebClient webClient) {
        this.webClient = webClient;
    }

    @RabbitListener(queues = "order_notification")
    public void handleMessage(String message) {
        if (message.isEmpty()) {
            return;
        }

        Map<String, Object> obj = emailServiceImplement.convertToObject(message);

        int user_id = (int) obj.get("user_id");
        String productName = (String) obj.get("product_name");

        String response = this.webClient.get()
                        .uri("/user/" + String.valueOf(user_id))
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

        Map<String, Object> user = emailServiceImplement.convertToObject(response);
        String content = emailServiceImplement.constructOrderContent(productName, (String) user.get("username"));
        emailServiceImplement.sendEmail(content, (String) user.get("email"), "Notificação teste");
        System.out.println("Mensagem enviada com sucesso!");
    }
}
