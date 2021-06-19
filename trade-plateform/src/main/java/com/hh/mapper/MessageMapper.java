package com.hh.mapper;

import com.hh.pojo.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MessageMapper {
    /**
     * 获取两个人的对话
     * @param user1Id 用户1
     * @param user2Id 用户2
     * @return 对话列表
     */
    List<Message> getMessageByIds(@Param("buyId") int user1Id, @Param("sellId") int user2Id);

    /**
     * 添加一条消息
     * @param message 消息
     * @return 添加的记录条数
     */
    int addMessage(Message message);

    List<Message> getMessageByUserId(int userId);

    List<Integer> getUserIds(int adminId);
}
