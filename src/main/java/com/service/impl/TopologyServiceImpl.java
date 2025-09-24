package com.service.impl;

import com.entity.Topology;
import com.mapper.TopologyMapper;
import com.service.TopologyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author cwj
 * @since 2025-09-24
 */
@Service
public class TopologyServiceImpl extends ServiceImpl<TopologyMapper, Topology> implements TopologyService {
    @Override
    public void insertOne(Topology topology) {
        this.save(topology);
    }
}
