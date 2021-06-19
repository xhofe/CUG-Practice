package com.hh.service.impl;

import com.hh.mapper.GoodsMapper;
import com.hh.pojo.Goods;
import com.hh.pojo.Type;
import com.hh.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {
    private GoodsMapper goodsMapper;

    @Autowired
    public void setGoodsMapper(GoodsMapper goodsMapper){
        this.goodsMapper = goodsMapper;
    }


    @Override
    public int addGood(Goods goods) {
        return goodsMapper.addGoods(goods);
    }

    @Override
    public int updateGood(Goods goods) {
        return goodsMapper.updateGoods(goods);
    }

    @Override
    public int deleteGood(int goodsId) {
        return goodsMapper.deleteGoods(goodsId);
    }


    @Override
    public List<Goods> getGoodsOrderByPop() {
        return goodsMapper.getGoodsOrderByTop();
    }

    @Override
    public List<Goods> searchGoods(Map map) {
        return goodsMapper.SearchGoods(map);
    }

    @Override
    public List<Type> getAllType() {
        return goodsMapper.getAllType();
    }

    @Override
    public int addType(Type type) {
        return goodsMapper.addType(type);
    }

    @Override
    public int deleteType(int typeId) {
        return goodsMapper.deleteType(typeId);
    }

    @Override
    public int updateType(Type type) {
        return goodsMapper.updateType(type);
    }

    @Override
    public List<Goods> getAllGoods() {
        return goodsMapper.getAllGoods();
    }

    @Override
    public Goods getGoodsById(int goodsId) {
        return goodsMapper.getGoodsByGoodsId(goodsId);
    }

    @Override
    public List<Goods> getGoodsByType(int typeId) {
        return goodsMapper.getGoodsByTypeId(typeId);
    }
}
