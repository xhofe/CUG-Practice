package com.hh.service;


import com.hh.pojo.User;
import com.hh.pojo.UserDetails;

public interface UserService {

    public UserDetails login(User user);

    public int addUser(User user);

    public boolean exitsUser(String email);

    public int updateUser(User user);

    public User getUserById(int userId);
}
