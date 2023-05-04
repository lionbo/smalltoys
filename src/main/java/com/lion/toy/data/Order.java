package com.lion.toy.data;

import lombok.Data;

import java.util.Date;

@Data
public class Order {
    private Date tradeDate;
    private String target;
    private int type;//1 buy;2 sell
    private Double price;
    private Long numbers;
}
