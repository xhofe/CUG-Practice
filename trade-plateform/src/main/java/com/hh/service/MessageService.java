package com.hh.service;

import com.hh.pojo.Message;
import io.swagger.models.auth.In;

import java.util.List;

public interface MessageService {
    public int sendMessage(Message message);
    public List<Message> getList(int userId);
    public List<Message> getList(int adminId,int userId);
    public List<Integer> getUserIds(int adminId);
}
