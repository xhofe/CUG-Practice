package com.hh;

import com.hh.pojo.Message;
import com.hh.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MessageTest {
    @Autowired
    private MessageService messageService;

    @Test
    public void test1(){
        System.out.println(messageService.getUserIds(1));
    }
}
