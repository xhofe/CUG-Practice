package com.hh.service;

import com.hh.pojo.Admin;
import com.hh.pojo.UserDetails;

import java.util.List;

public interface AdminService {
    List<Admin> getAdmins();
    Admin getAdminByEmail(String email);
    UserDetails login(Admin admin);
}
