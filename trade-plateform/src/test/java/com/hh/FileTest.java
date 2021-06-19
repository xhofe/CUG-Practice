package com.hh;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
public class FileTest {
    @Test
    void testFilePath(){
        File file=new File("a.txt");
        System.out.println(file.getAbsolutePath());
    }
}
