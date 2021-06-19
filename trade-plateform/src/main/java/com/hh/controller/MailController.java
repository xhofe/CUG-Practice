package com.hh.controller;

import com.hh.service.MailService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

@RestController
@RequestMapping("/api/mail")
public class MailController {
    //@Autowired
    private MailService mailService;
    private static final String Key = "chenRan";

    @RequestMapping("getCheckCode")
    @ResponseBody
    public String getCheckCode(String email){
        String randomNum = String.valueOf(new Random().nextInt(899999)+100000);;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmsss");
        Calendar calendar = Calendar.getInstance();
        calendar.add(calendar.MINUTE,5);//+5分钟

        String afterTime = simpleDateFormat.format(calendar.getTime());//5分钟以后的时间
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update((Key+"@"+afterTime+"@"+randomNum).getBytes());
            String md5 = new BigInteger(1,md.digest()).toString(16);

            String message = "您的注册验证码为："+ md5;
            mailService.sendSimpleMail(email,"注册验证码",message);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "success";
    }

    @RequestMapping("verifyCode")
    @ResponseBody
    public String verifyCode(String hash,String afterTime,String code){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update((Key+"@"+afterTime+"@"+ code).getBytes());
            String md5 = new BigInteger(1,md.digest()).toString(16);

            if(md5.equalsIgnoreCase(hash))
                return "success";//校验成功
            else
                return "error";//todo:完善判断相等的操作
        }catch (Exception e){
            e.printStackTrace();
        }
        return "success";
    }
}
