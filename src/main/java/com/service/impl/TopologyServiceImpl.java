package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.Query;
import com.entity.Edge;
import com.entity.Topology;
import com.entity.TopologyType;
import com.mapper.TopologyMapper;
import com.service.EdgeService;
import com.service.MessageService;
import com.service.TopologyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.service.TopologyTypeService;
import com.vo.TopologyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    TopologyTypeService topologyTypeService;

    @Autowired
    MessageService messageService;

    @Autowired
    EdgeService edgeService;

    @Override
    public void insertOne(Topology topology) {
        this.save(topology);
    }

    public List<TopologyVo> getByPage(Map<String, Object> params) {
        IPage page = this.page(new Query<Topology>().getPage(params,"Time",false));
        List<Topology> list = page.getRecords();

        List<Integer> typeId = list.stream().map(obj->obj.getTypeId()).collect(Collectors.toList());
        Map<Integer,TopologyType> topologyTypeList = topologyTypeService.list(new LambdaQueryWrapper<TopologyType>().in(TopologyType::getTypeId, typeId)).stream()
                .collect(Collectors.toMap(TopologyType::getTypeId,obj->obj));

        List<Integer> edgeId = list.stream().map(obj->obj.getEdgeId()).collect(Collectors.toList());
        Map<Integer,Edge> edges = edgeService.list(new LambdaQueryWrapper<Edge>().in(Edge::getEdgeId, edgeId)).stream().collect(Collectors.toMap(Edge::getEdgeId,obj->obj));


        List<TopologyVo> collect = list.stream().map(obj -> {
            TopologyVo topologyVo = new TopologyVo(obj);
            topologyVo.setType(topologyTypeList.get(obj.getTypeId()));
            topologyVo.setEdge(edges.get(obj.getEdgeId()));
            topologyVo.setName(topologyTypeList.get(obj.getTypeId()).getName());
            return topologyVo;
        }).collect(Collectors.toList());
        collect.stream().forEach(topology -> {
            System.out.println(topology);
        });
        return collect;
    }
}
