package com.service;

import com.entity.Topology;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author cwj
 * @since 2025-09-24
 */
public interface TopologyService extends IService<Topology> {
    public void insertOne(Topology topology);
}
