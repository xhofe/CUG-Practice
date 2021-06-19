package com.hh;

import com.hh.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OrderTest {
    @Autowired
    private OrderService orderService;

    @Test
    public void test1(){
        System.out.println(orderService.getOrdersByUserId(1));
    }
}
