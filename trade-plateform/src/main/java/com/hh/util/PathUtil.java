package com.hh.util;

import org.springframework.boot.system.ApplicationHome;

import java.io.File;

public class PathUtil {
    static {
//        ApplicationHome h = new ApplicationHome(PathUtil.class);
//        File jarF = h.getSource();
//        ROOT_PATH=jarF.getParentFile().toString();
        ROOT_PATH="/home/Xhofe/personal-mall";
    }
    private static String ROOT_PATH;
    public static String getRootPath(){
        return ROOT_PATH;
    }
}
