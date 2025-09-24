package com.controller;


import com.entity.Topology;
import com.mapper.TopologyMapper;
import com.service.TopologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    public void TopologyAdd(){
        Topology topology = new Topology();
        topology.setName("<UNK>");
        topologyService.insertOne(topology);
        return;
    }
}

