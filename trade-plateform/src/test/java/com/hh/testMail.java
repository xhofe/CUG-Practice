package com.hh;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@SpringBootTest
public class testMail {
    @Autowired
    JavaMailSenderImpl mailSender;

    @Test
    void testOneEmail(){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("主题");
        message.setText("这是一个邮件测试");
        message.setTo("liuziqi@cug.edu.cn");
        message.setFrom("709124735@qq.com");

        mailSender.send(message);
    }

}
