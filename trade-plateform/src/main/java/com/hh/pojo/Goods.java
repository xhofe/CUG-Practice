package com.hh.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Goods {

  private int goodsId;
  private String goodsName;
  private int pop;
  private double price;
  private double secondPrice;
  private int num;
  private String introduce;
  private int status;
  private int typeId;
  private String imgurl;
}
