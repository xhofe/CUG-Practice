package com.hh.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Comments {

    private int id;
    private int userId;
    private int goodsId;
    private String comment;
    private java.sql.Timestamp time;
    private int orderId;
}
