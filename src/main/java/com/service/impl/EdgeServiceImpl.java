package com.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entity.ComputingCentral;
import com.entity.Edge;
import com.mapper.ComputingCentralMapper;
import com.mapper.EdgeMapper;
import com.service.EdgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EdgeServiceImpl extends ServiceImpl<EdgeMapper, Edge>
        implements EdgeService {

    @Autowired
    private ComputingCentralMapper computingCentralMapper;

    @Override
    @Transactional
    public Integer insertEdge(Integer fromCentral, Integer toDevice) {  // 修改参数名
        Edge edge = new Edge();
        edge.setFromCentral(fromCentral);
        edge.setToDevice(toDevice);  // 修改字段名

        this.save(edge);
        return edge.getEdgeId();
    }

    @Override
    public Integer getLatestCentralIdByName(String centralName) {
        ComputingCentral latestCenter = computingCentralMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ComputingCentral>()
                        .eq("central_name", centralName)
                        .orderByDesc("central_id")
                        .last("LIMIT 1")
        );

        return latestCenter != null ? latestCenter.getCentralId() : null;
    }
}