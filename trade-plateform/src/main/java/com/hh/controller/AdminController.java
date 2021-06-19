package com.hh.controller;

import com.hh.enums.ResponseStatus;
import com.hh.pojo.Admin;
import com.hh.pojo.UserDetails;
import com.hh.service.AdminService;
import com.hh.util.CookieUtil;
import com.hh.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController extends BaseController{
    private AdminService adminService;

    @Autowired
    public void setAdminService(AdminService adminService){
        this.adminService = adminService;
    }

    @PostMapping("login")
    public Object login(@RequestBody Admin admin, HttpServletRequest request, HttpServletResponse response){
        UserDetails userDetails = adminService.login(admin);
        if(userDetails == null)
            return ResultUtil.fail(ResponseStatus.USERNAME_PASS_ERROR);
        String token = jwtTokenUtil.generateToken(userDetails);
        CookieUtil.setCookie(response,"Authorization",token);
        Map<String,String> map = new HashMap<>(1);
        map.put("token",token);
        return ResultUtil.ok(map);
    }

    @PostMapping("logout")
    public Object logout(HttpServletRequest request,HttpServletResponse response){
        UserDetails userDetails = getUserDetails(request);
        if (userDetails == null) {
            return ResultUtil.fail(ResponseStatus.NO_LOGIN);
        }
        CookieUtil.setCookie(response,"Authorization",null);
        return ResultUtil.ok();
    }
}
