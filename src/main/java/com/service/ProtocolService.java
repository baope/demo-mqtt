package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.Protocol;
import com.vo.ProtocolVo;

import java.util.List;
import java.util.Map;

public interface ProtocolService extends IService<Protocol> {

    /**
     * 插入协议数据
     */
    Integer insertProtocol(String protocolName, String value, Integer messageId);

    public List<ProtocolVo> getByPage(Map<String, Object> params);
}