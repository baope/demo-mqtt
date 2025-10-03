package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.Query;
import com.entity.*;
import com.mapper.ProtocolMapper;
import com.service.EdgeService;
import com.service.MessageService;
import com.service.ProtocolService;
import com.vo.ProtocolField;
import com.vo.ProtocolVo;
import com.vo.TopologyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProtocolServiceImpl extends ServiceImpl<ProtocolMapper, Protocol>
        implements ProtocolService {

    @Autowired
    public MessageService messageService;

    @Override
    public Integer insertProtocol(String protocolName, String value, Integer messageId) {
        Protocol protocol = new Protocol();
        protocol.setProtocolName(protocolName);
        protocol.setValue(value);
        protocol.setMessageId(messageId);
        protocol.setTimestamp(LocalDateTime.now());

        this.save(protocol);
        return protocol.getProtocolId();
    }
    public List<ProtocolVo> getByPage(Map<String, Object> params)
    {
        long start = System.currentTimeMillis();

        IPage page = this.page(new Query<Protocol>().getPage(params,"timestamp",false));
        List<Protocol> list = page.getRecords();

        List<Integer> typeId = list.stream().map(obj->obj.getMessageId()).collect(Collectors.toList());
        Map<Integer, Message> messageMap = messageService.list(new LambdaQueryWrapper<Message>().in(Message::getMessageId, typeId)).stream()
                .collect(Collectors.toMap(Message::getMessageId,obj->obj));


        List<ProtocolVo> collect = list.stream().map(obj -> {
            ProtocolVo protocolVo = new ProtocolVo(obj);

            Message message = messageMap.get(obj.getMessageId());
            String raw = message.getValue();
            String[] lines = raw.split("\\n");
            List<ProtocolField> list_message = new ArrayList<>();
            for (String line : lines) {
                if (line.contains(":")) {
                    String[] kv = line.split(":", 2); // 只分割一次，避免值里再有冒号
                    ProtocolField vo = new ProtocolField();
                    vo.setName(kv[0].trim());
                    vo.setValue(kv[1].trim());
                    list_message.add(vo);
                }
            }
            long end = System.currentTimeMillis();
            protocolVo.setMessage(list_message);
            protocolVo.setParse_time(Double.valueOf((end-start)/1000.0));
            return protocolVo;
        }).collect(Collectors.toList());
        collect.stream().forEach(topology -> {
            System.out.println(topology);
        });
        return collect;
    }
}