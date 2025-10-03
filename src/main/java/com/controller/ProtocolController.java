package com.controller;

import com.entity.Protocol;
import com.service.ProtocolService;
import com.vo.ProtocolVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/protocol")
public class ProtocolController {

    @Autowired
    private ProtocolService protocolService;

    @GetMapping
    public List<Protocol> getAllProtocols() {
        return protocolService.list();
    }

    @GetMapping("/{id}")
    public Protocol getProtocolById(@PathVariable Integer id) {
        return protocolService.getById(id);
    }

    @PostMapping("getBypage")
    public List<ProtocolVo> getProtocolByPage(@RequestBody HashMap<String, Object> parms) {
        return protocolService.getByPage(parms);
    }
}