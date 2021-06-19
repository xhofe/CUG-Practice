package com.hh.mapper;

import com.hh.pojo.Comments;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CommentsMapper {
    /**
     * 获取一个物品的评价
     * @param goodsId 物品ID
     * @return 评价
     */
    List<Comments> getCommentsByGoodsId(int goodsId);

    /**
     * 添加一个评价
     * @param comments 评价
     * @return 添加的记录条数
     */
    int addGoodsComments(Comments comments);

    /**
     * 删除一个评价
     * @param id 评价的ID
     * @return 被操作的记录条数
     */
    int deleteComments(int id);

    Comments getCommentById(int orderId);
}
