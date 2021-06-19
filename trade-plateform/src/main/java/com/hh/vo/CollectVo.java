package com.hh.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectVo {
    private int id;
    private String icon;
    private String name;
    private double price;
    private int quantity;
    private boolean checked;
}
