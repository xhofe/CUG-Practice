package com.hh.service;

import com.hh.pojo.Goods;
import com.hh.pojo.Type;

import java.util.List;

import java.util.List;
import java.util.Map;

public interface GoodsService {
    //添加
     int addGood(Goods goods);

    //更新
     int updateGood(Goods goods);

    //删除
     int deleteGood(int goodsId);


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

     List<Goods> searchGoods(Map map);

    /**
     * 获取所有Goods
     * @return Goods
     */
     List<Goods> getAllGoods();

     Goods getGoodsById(int goodsId);

     List<Goods> getGoodsByType(int typeId);

     List<Goods> getGoodsOrderByPop();
}
