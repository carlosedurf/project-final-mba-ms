package com.store.payment.service.implement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.payment.domain.Payment;
import com.store.payment.repository.PaymentRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PaymentServiceImplement extends GenericServiceImplement {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail_from}")
    private String mailFrom;

    @Autowired
    private PaymentRepository paymentRepository;

    public Map<String, Object> convertToObject(String jsonS) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(jsonS, Map.class);
            return map;
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public String constructOrderContent(String username) {
        return "<html><body><h1>Olá " + username + "</h1><p>Você acaba de realizar o pagamento via PAY TEST </p></body></html>";
    }

    public void sendEmail(String content, String email, String subject) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom(this.mailFrom);
//        message.setTo(email);
//        message.setSubject(subject);
//        message.setText(content);
//        mailSender.send(message);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(this.mailFrom);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            System.out.println(e);
        }
    }

    public void save(Payment payment) {
        paymentRepository.save(payment);
    }
}
