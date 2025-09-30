package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.Protocol;

public interface ProtocolService extends IService<Protocol> {

    /**
     * 插入协议数据
     */
    Integer insertProtocol(String protocolName, String value, Integer messageId);
}