package com.store.payment.component.implement;

import com.store.payment.component.RabbitMQComponent;
import com.store.payment.domain.Payment;
import com.store.payment.service.implement.PaymentServiceImplement;
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
    private PaymentServiceImplement paymentServiceImplement;

    private final WebClient webClient;

    public RabbitMQComponentImplement(WebClient webClient) {
        this.webClient = webClient;
    }

    public String sendPayment() {
        System.out.println("Realiza integração com ferramenta externa");
        return "RETORNA_HASH_DA_INTEGRACAO";
    }

    @RabbitListener(queues = "payment_notification")
    public void handleMessage(String message) {
        if (message.isEmpty()) {
            return;
        }

        Map<String, Object> obj = paymentServiceImplement.convertToObject(message);

        int user_id = (int) obj.get("user_id");

        String response = this.webClient.get()
                        .uri("/user/" + String.valueOf(user_id))
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

        String hashIntegration = this.sendPayment();
        Payment payment = new Payment();
        payment.setHashTransaction(hashIntegration);

        this.paymentServiceImplement.save(payment);

        Map<String, Object> user = paymentServiceImplement.convertToObject(response);
        String content = paymentServiceImplement.constructOrderContent((String) user.get("username"));
        paymentServiceImplement.sendEmail(content, (String) obj.get("email"), "Sucesso no pagamento");

        this.webClient.post()
                .uri("http://localhost:8086/api/shopping/success/" + String.valueOf(obj.get("id")))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println("Mensagem enviada com sucesso!");
    }
}
