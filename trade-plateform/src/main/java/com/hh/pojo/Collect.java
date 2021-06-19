package com.hh.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Collect {

  private int collectId;
  private int userId;
  private int goodsId;
  private int count;
  private int checked;
}
