package com.hh.pojo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserDetails {
    public int userId;
    public String userName;
    public boolean admin;
}
