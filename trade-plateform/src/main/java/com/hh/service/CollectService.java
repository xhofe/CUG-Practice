package com.hh.service;

import com.hh.pojo.Collect;
import com.hh.vo.CollectVo;

import java.util.List;

public interface CollectService {
    public Collect hasCollect(Collect collect);
    public int addCollect(Collect collect);
    public int deleteCollect(int id);
    public List<Collect> getCollectsByUserId(int userId);
    public int updateOneCheck(int id,boolean checked);
    public int updateAllCheck(int userId,boolean checked);
    public int updateCount(CollectVo collectVo);
}
