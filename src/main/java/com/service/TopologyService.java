package com.service;

import com.entity.Topology;
import com.baomidou.mybatisplus.extension.service.IService;
import com.vo.TopologyVo;

import java.util.List;
import java.util.Map;

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
    public List<TopologyVo> getByPage(Map<String, Object> params);
}
