package com.demo.order_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.demo.order_service.entity.Orderss;
import com.demo.order_service.pojo.OrderPojo;
import com.demo.order_service.service.OrderService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    OrderService orderService;
    

    // Endpoint to create a new order
    @PostMapping("")
    public Mono<ResponseEntity<OrderPojo>> createOrder(@RequestBody OrderPojo order) {
        return orderService.addAOrder(order)
                           .map(savedOrder -> ResponseEntity.status(HttpStatus.CREATED).body(savedOrder))  // Success: return 201 Created
                           .defaultIfEmpty(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());  // Failure: return 400 Bad Request
    }
}
