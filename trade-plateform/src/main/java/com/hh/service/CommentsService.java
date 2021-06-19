package com.hh.service;

import com.hh.pojo.Comments;

import java.util.List;

public interface CommentsService {
    public List<Comments> getCommentsByGoodsId(int goodsId);
    public int addGoodsComment(Comments comments);
    public boolean hasComment(int id);
}
