package com.hh.mapper;

import com.hh.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserMapper {
    /**
     * 获取所有用户
     * @return 用户列表
     */
    List<User> getAllUsers();

    /**
     * 根据用户ID获取用户
     * @param userId 用户ID
     * @return 用户
     */
    User getUserById(int userId);

    /**
     * 添加用户
     * @param user 用户
     * @return 插入的记录数目
     */
    int addUser(User user);

    /**
     * 根据名字获取用户列表
     * @param userName 名字
     * @return 用户列表
     */
    List<User> findUserName(String userName);

    /**
     * 根据邮箱获取用户列表
     * @param userEmail 名字
     * @return 用户列表
     */
    List<User> findUserEmail(String userEmail);

//    int updateUser(Map<String,Object> map);

    /**
     * 修改用户信息
     * @param user 用户
     * @return 被操作的记录条数
     */
    int updateUser(User user);

    /**
     * 根据ID删除一个用户
     * @param userId 用户ID
     * @return 被操作的记录条数
     */
    int deleteUser(int userId);
}
