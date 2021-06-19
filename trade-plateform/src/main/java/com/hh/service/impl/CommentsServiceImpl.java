package com.hh.service.impl;

import com.hh.mapper.CommentsMapper;
import com.hh.pojo.Comments;
import com.hh.service.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentsServiceImpl implements CommentsService {

    private CommentsMapper mapper;

    @Autowired
    public void setMapper(CommentsMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<Comments> getCommentsByGoodsId(int goodsId) {
        return mapper.getCommentsByGoodsId(goodsId);
    }

    @Override
    public int addGoodsComment(Comments comments) {
        return mapper.addGoodsComments(comments);
    }

    @Override
    public boolean hasComment(int id) {
        Comments comments=mapper.getCommentById(id);
        return comments!=null;
    }
}
