package com.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entity.Protocol;
import com.mapper.ProtocolMapper;
import com.service.ProtocolService;
import org.springframework.stereotype.Service;

@Service
public class ProtocolServiceImpl extends ServiceImpl<ProtocolMapper, Protocol>
        implements ProtocolService {

    @Override
    public Integer insertProtocol(String protocolName, String value, Integer messageId) {
        Protocol protocol = new Protocol();
        protocol.setProtocolName(protocolName);
        protocol.setValue(value);
        protocol.setMessageId(messageId);

        this.save(protocol);
        return protocol.getProtocolId();
    }
}