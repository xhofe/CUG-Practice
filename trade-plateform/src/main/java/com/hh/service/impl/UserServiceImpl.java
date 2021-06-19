package com.hh.service.impl;

import com.hh.mapper.UserMapper;
import com.hh.pojo.User;
import com.hh.pojo.UserDetails;
import com.hh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User getUserById(int userId) {
        return userMapper.getUserById(userId);
    }

    public UserDetails login(User user){
        List<User> users=userMapper.findUserEmail(user.getUserEmail());
        if (users.size()==0||!user.getUserPassword().equals(users.get(0).getUserPassword())){
            return null;
        }
        UserDetails userDetails=new UserDetails();
        userDetails.setUserId(users.get(0).getUserId());
        userDetails.setUserName(users.get(0).getUserName());
        userDetails.setAdmin(false);
        return userDetails;
    }

    public int addUser(User user){
        return userMapper.addUser(user);
    }

    public boolean exitsUser(String email){
        List<User> userList=userMapper.findUserEmail(email);
        return userList.size()!=0;
    }

    public int updateUser(User user){
        return userMapper.updateUser(user);
    }
}
