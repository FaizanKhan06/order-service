package com.demo.order_service.pojo;

import java.time.LocalDate;

import com.demo.order_service.entity.Payments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPojo {

    private int id;
    private LocalDate orderDate;
    private double orderAmt;
    private Payments payment;

}
