package com.hh;

import com.hh.mapper.UserMapper;
import com.hh.pojo.User;
import com.hh.enums.ResponseStatus;
import com.hh.util.ResultUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.SQLException;

@SpringBootTest
class TradePlateformApplicationTests {

    @Autowired
    DataSource dataSource;

    @Autowired
    UserMapper userMapper;

    @Test
    void contextLoads() throws SQLException {
        System.out.println(dataSource.getConnection()) ;
    }

    @Test
    void testUser(){
        User user=new User();
        user.setUserName("xhf");
        user.setUserPassword("666");
        user.setUserEmail("xhf@hh.com");
        System.out.println(userMapper.addUser(user));
    }

    @Test
    void testUpdateUser(){
        User user=new User();
        user.setUserId(5);
        user.setUserPassword("666hh");
        System.out.println(userMapper.  updateUser(user));
    }

    @Test
    void testResultUtil(){
        System.out.println(ResultUtil.ok());
        System.out.println(ResultUtil.ok("yes!"));
        System.out.println(ResultUtil.fail(ResponseStatus.FORBIDDEN));
        System.out.println(ResultUtil.error());
    }
    //test user servic
}