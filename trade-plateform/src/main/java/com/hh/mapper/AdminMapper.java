package com.hh.mapper;

import com.hh.pojo.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface AdminMapper {
    /**
     * 获取所有管理员
     * @return 管理员列表
     */
    List<Admin> getAllAdmins();

    /**
     * 根据ID获取管理员
     * @param id 管理员ID
     * @return 管理员
     */
    Admin getAdminById(int id);

    /**
     * 根据email获取管理员
     * @param email
     * @return
     */
    Admin getAdminByEmail(String email);

    /**
     * 添加管理员
     * @param admin 管理员
     * @return 添加的记录条数
     */
    int addAdmin(Admin admin);

    /**
     * 根据ID删除管理员
     * @param id 管理员ID
     * @return 被操作的记录条数
     */
    int deleteAdmin(int id);

    /**
     * 修改管理员信息
     * @param admin 管理员
     * @return 被操作的记录条数
     */
    int updateAdmin(Admin admin);


}
