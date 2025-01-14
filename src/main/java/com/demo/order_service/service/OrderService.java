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

    public OrderPojo getOrderById(int id){
        Orderss order = repo.findById(id).orElse(null);

        if(order != null){
            OrderPojo pojo = new OrderPojo();
            BeanUtils.copyProperties(order, pojo);
            Mono<Payments> paymentMono = someRestCall(order.getPaymentId());
            pojo.setPayment(paymentMono.block());
            return pojo;
        }

        return null;
    }

    public Mono<OrderPojo> addAOrder(OrderPojo order) {
        Orderss orders = new Orderss();
        orders.setOrderAmt(order.getOrderAmt());
        orders.setOrderDate(order.getOrderDate());
        orders.setPaymentId(order.getPayment().getId());
        
        orders = repo.saveAndFlush(orders);
        order.setId(orders.getId());
        
        Payments payment = new Payments();
        
        payment.setOrderId(order.getId());
        payment.setCardNumber(order.getPayment().getCardNumber());
        
        kafkaTemplate.send(AppConstant.TOPIC_ORDERS, payment.toString());
        

        Mono<Integer> paymentId = someRestCall2();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(paymentId.block());
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

        orders.setPaymentId(paymentId.block());
        orders = repo.saveAndFlush(orders);

        Mono<Payments> paymentMono = someRestCall(paymentId.block());

        return paymentMono.flatMap(paymentsss -> {
            order.setPayment(paymentsss);
            return Mono.just(order);
        });
    }

    public Mono<Payments> someRestCall(int id) {
        return this.webClient.get()
                             .uri("/api/payments/{id}", id)
                             .retrieve()
                             .bodyToMono(Payments.class);
    }

    public Mono<Integer> someRestCall2() {
        return this.webClient.get()
                             .uri("/api/payments/lastId")
                             .retrieve()
                             .bodyToMono(Integer.class);
    }
}
