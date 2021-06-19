package com.hh.service.impl;

import com.hh.mapper.MessageMapper;
import com.hh.pojo.Message;
import com.hh.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    private MessageMapper mapper;

    @Autowired
    public void setMapper(MessageMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public int sendMessage(Message message) {
        return mapper.addMessage(message);
    }

    @Override
    public List<Message> getList(int userId) {
        return mapper.getMessageByUserId(userId);
    }

    @Override
    public List<Integer> getUserIds(int adminId) {
        return mapper.getUserIds(adminId);
    }

    @Override
    public List<Message> getList(int adminId, int userId) {
        return mapper.getMessageByIds(userId,adminId);
    }
}
