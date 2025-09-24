package com.controller;


import com.entity.Topology;
import com.mapper.TopologyMapper;
import com.service.TopologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

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
}

