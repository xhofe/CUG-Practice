package com.hh.service.impl;

import com.hh.mapper.CollectMapper;
import com.hh.pojo.Collect;
import com.hh.service.CollectService;
import com.hh.vo.CollectVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectServiceImpl implements CollectService {

    private CollectMapper mapper;

    @Autowired
    public void setMapper(CollectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public int updateOneCheck(int id, boolean checked) {
        Collect collect=mapper.getCollectById(id);
        collect.setChecked(checked?1:0);
        return mapper.updateCollect(collect);
    }

    @Override
    public int updateCount(CollectVo collectVo) {
        Collect collect=mapper.getCollectById(collectVo.getId());
        collect.setCount(collectVo.getQuantity());
        return mapper.updateCollect(collect);
    }

    @Override
    public int updateAllCheck(int userId, boolean checked) {
        int res=0;
        List<Collect> collects=getCollectsByUserId(userId);
        for (Collect collect : collects) {
            collect.setChecked(checked?1:0);
            res+=mapper.updateCollect(collect);
        }
        return res;
    }

    @Override
    public List<Collect> getCollectsByUserId(int userId) {
        return mapper.getCollectsByUserId(userId);
    }

    @Override
    public int deleteCollect(int id) {
        return mapper.deleteCollectById(id);
    }

    @Override
    public int addCollect(Collect collect) {
        return mapper.addCollect(collect);
    }

    @Override
    public Collect hasCollect(Collect collect) {
        List<Collect> collects=mapper.getCollectsByUserId(collect.getUserId());
        for (Collect collect1 : collects) {
            if (collect.getGoodsId()==collect1.getGoodsId()){
                return collect1;
            }
        }
        return null;
    }
}
