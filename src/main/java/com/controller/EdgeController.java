package com.controller;

import com.entity.Edge;
import com.service.EdgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/edge")
public class EdgeController {

    @Autowired
    private EdgeService edgeService;

    @GetMapping
    public List<Edge> getAllEdges() {
        return edgeService.list();
    }

    @GetMapping("/{id}")
    public Edge getEdgeById(@PathVariable Integer id) {
        return edgeService.getById(id);
    }

    @PostMapping
    public Map<String, Object> createEdge(@RequestBody Edge edge) {
        Map<String, Object> result = new HashMap<>();
        boolean success = edgeService.save(edge);
        result.put("success", success);
        result.put("data", success ? edge : null);
        return result;
    }

    /**
     * 获取LoRaWan中心的ID
     * GET /edge/lorawan
     */
    @GetMapping("/lorawan")
    public Map<String, Object> getLoRaWanCentralId() {
        Map<String, Object> result = new HashMap<>();
        // 使用新的方法名并传入中心名称
        Integer centralId = edgeService.getLatestCentralIdByName("LoRaWan中心");
        result.put("loRaWanCentralId", centralId);
        result.put("success", centralId != null);
        return result;
    }

    /**
     * 根据中心名称获取中心ID
     * GET /edge/center/{centerName}
     */
    @GetMapping("/center/{centerName}")
    public Map<String, Object> getCentralIdByName(@PathVariable String centerName) {
        Map<String, Object> result = new HashMap<>();
        Integer centralId = edgeService.getLatestCentralIdByName(centerName);
        result.put("centerName", centerName);
        result.put("centralId", centralId);
        result.put("success", centralId != null);
        result.put("message", centralId != null ? "获取成功" : "未找到该中心");
        return result;
    }

    /**
     * 获取所有中心的ID
     * GET /edge/centers
     */
    @GetMapping("/centers")
    public Map<String, Object> getAllCenterIds() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Integer> centerIds = new HashMap<>();

        // 获取三个中心的ID
        Integer loRaWanId = edgeService.getLatestCentralIdByName("LoRaWan中心");
        Integer wifiId = edgeService.getLatestCentralIdByName("WiFi中心");
        Integer nbIotId = edgeService.getLatestCentralIdByName("NB-IoT中心");

        centerIds.put("LoRaWan中心", loRaWanId);
        centerIds.put("WiFi中心", wifiId);
        centerIds.put("NB-IoT中心", nbIotId);

        result.put("success", true);
        result.put("centerIds", centerIds);
        return result;
    }
}