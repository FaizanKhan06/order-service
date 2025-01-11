package com.demo.order_service.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.demo.order_service.constant.AppConstant;
import com.demo.order_service.entity.Orderss;
import com.demo.order_service.entity.Payments;
import com.demo.order_service.pojo.OrderPojo;
import com.demo.order_service.repo.OrderRepo;

import reactor.core.publisher.Mono;

@Service
public class OrderService {

    @Autowired
    OrderRepo repo;

    private final WebClient webClient;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public OrderService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:1010").build();
    }

    public Mono<OrderPojo> addAOrder(Orderss order) {
        OrderPojo pojo = new OrderPojo();
        BeanUtils.copyProperties(order, pojo);

        Mono<Payments> paymentMono = someRestCall(order.getPaymentId());

        return paymentMono.flatMap(payment -> {
            pojo.setPayment(payment); 
            Orderss latest = repo.saveAndFlush(order);
            pojo.setId(latest.getId());
            kafkaTemplate.send(AppConstant.TOPIC_ORDERS, pojo.toString());
            return Mono.just(pojo);
        });
    }

    public Mono<Payments> someRestCall(int id) {
        return this.webClient.get()
                             .uri("/api/payments/{id}", id)
                             .retrieve()
                             .bodyToMono(Payments.class);
    }
}
