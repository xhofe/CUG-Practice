package com.hh.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order {

  private int orderId;
  private int goodsId;
  private int buyUserId;
  private String name;
  private String address;
  private java.sql.Timestamp time;
  private double cost;
  private int status;
  private String trackingNumber;
}
