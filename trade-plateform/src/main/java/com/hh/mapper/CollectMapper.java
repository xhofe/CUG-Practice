package com.hh.mapper;

import com.hh.pojo.Collect;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CollectMapper {
    /**
     * 获取一个用户的所有收藏
     * @param userId 用户ID
     * @return 收藏列表
     */
    List<Collect> getCollectsByUserId(int userId);

    /**
     * 删除一个收藏
     * @param id 收藏的ID
     * @return 被操作的记录条数
     */
    int deleteCollectById(int id);

    /**
     * 添加一个收藏
     * @param collect 收藏
     * @return 添加的记录条数
     */
    int addCollect(Collect collect);

    Collect getCollectById(int id);

    int updateCollect(Collect collect);
}
