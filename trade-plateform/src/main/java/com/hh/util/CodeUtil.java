package com.hh.util;

import org.springframework.context.annotation.Bean;

import java.util.Random;

public class CodeUtil {
    private static Random random=new Random();
    public static String randomCode() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }

}
