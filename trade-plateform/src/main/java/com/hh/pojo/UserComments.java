package com.hh.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserComments {

  private int id;
  private String comments;
  private int userId;
  private int commentUserId;
  private java.sql.Timestamp time;
}
