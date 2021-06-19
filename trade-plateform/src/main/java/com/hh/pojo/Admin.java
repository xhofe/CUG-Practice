package com.hh.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Admin {

  private int adminId;
  private String adminName;
  private String adminPassword;
  private String adminEmail;
  private String adminPhone;
  private String adminAvatar;
}
