package com.hh.mapper;

import com.hh.pojo.Goods;
import com.hh.pojo.Type;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface GoodsMapper {
    /**
     * 获取所有物品
     * @return 物品列表
     */
    List<Goods> getAllGoods();

    /**
     * 根据GoodsId获取goods
     * @param goodsId 商品ID
     * @return 商品
     */
    Goods getGoodsByGoodsId(int goodsId);

    /**
     * 根据条件搜索物品
     * @param map 条件键值对
     * @return 物品列表
     * TODO
     */
    List<Goods> SearchGoods(Map map);

    /**
     * 添加物品
     * @param goods 物品
     * @return 添加的记录条数
     */
    int addGoods(Goods goods);

    /**
     * 修改物品信息
     * @param goods 物品
     * @return 被操作的记录条数
     */
    int updateGoods(Goods goods);

    /**
     * 删除一个物品
     * @param goodsId 物品ID
     * @return 被操作的记录条数
     */
    int deleteGoods(int goodsId);

    /**
     * 获取所有分类
     * @return 分类
     */
    List<Type> getAllType();


    /**
     * 添加分类
     * @param type
     * @return
     */
    int addType(Type type);

    /**
     * 删除分类
     * @param typeId
     * @return
     */
    int deleteType(int typeId);

    /**
     * 更新分类
     * @param type
     * @return
     */
    int updateType(Type type);

    List<Goods> getGoodsByTypeId(int typeId);

    List<Goods> getGoodsOrderByTop();
}
