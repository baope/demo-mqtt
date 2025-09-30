package com.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.entity.Edge;
import com.entity.Message;
import com.entity.Topology;
import com.entity.TopologyType;
import com.mapper.TopologyMapper;
import com.service.EdgeService;
import com.service.MessageService;
import com.service.TopologyService;
import com.service.TopologyTypeService;
import com.vo.TopologyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author cwj
 * @since 2025-09-24
 */
@RestController
@RequestMapping("/topology")
public class TopologyController {

    @Autowired
    TopologyService topologyService;



    @PostMapping("add")
    public void TopologyAdd(@RequestBody Topology topology) {
        topologyService.insertOne(topology);
        return;
    }

    @PostMapping("getBypage")
    public List<TopologyVo> getBypage(@RequestBody HashMap<String, Object> parms) {
        return topologyService.getByPage(parms);
    }


}

