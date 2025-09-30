package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.Message;

public interface MessageService extends IService<Message> {

    /**
     * 插入消息数据
     */
    Integer insertMessage(String value);
}