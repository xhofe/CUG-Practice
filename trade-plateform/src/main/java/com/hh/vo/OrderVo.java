package com.hh.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderVo {
    private int id;
    private String icon;
    private String name;
    private double price;
    private int quantity;
}
