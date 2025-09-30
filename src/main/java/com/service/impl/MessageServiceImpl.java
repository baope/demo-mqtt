package com.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entity.Message;
import com.mapper.MessageMapper;
import com.service.MessageService;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
        implements MessageService {

    @Override
    public Integer insertMessage(String value) {
        Message message = new Message();
        message.setValue(value);

        this.save(message);
        return message.getMessageId();
    }
}