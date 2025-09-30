package com.controller;

import com.entity.TopologyType;
import com.service.TopologyTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 拓扑类型前端控制器
 * </p>
 *
 * @author cwj
 * @since 2025-09-24
 */
@RestController
@RequestMapping("/topologyType")
public class TopologyTypeController {

    @Autowired
    private TopologyTypeService topologyTypeService;

    /**
     * 创建新的拓扑类型
     * POST /topologyType
     */
    @PostMapping
    public Map<String, Object> createTopologyType(@RequestBody TopologyType topologyType) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = topologyTypeService.save(topologyType);
            result.put("success", success);
            result.put("message", success ? "创建成功" : "创建失败");
            if (success) {
                result.put("data", topologyType);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "创建失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 根据ID获取拓扑类型
     * GET /topologyType/{id}
     */
    @GetMapping("/{id}")
    public Map<String, Object> getTopologyTypeById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            TopologyType topologyType = topologyTypeService.getById(id);
            if (topologyType != null) {
                result.put("success", true);
                result.put("data", topologyType);
            } else {
                result.put("success", false);
                result.put("message", "未找到ID为 " + id + " 的拓扑类型");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取所有拓扑类型
     * GET /topologyType/list
     */
    @GetMapping("/list")
    public Map<String, Object> getAllTopologyTypes() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<TopologyType> list = topologyTypeService.list();
            result.put("success", true);
            result.put("data", list);
            result.put("total", list.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 根据名称查询拓扑类型
     * GET /topologyType/name/{name}
     */
    @GetMapping("/name/{name}")
    public Map<String, Object> getTopologyTypeByName(@PathVariable String name) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<TopologyType> list = topologyTypeService.lambdaQuery()
                    .eq(TopologyType::getName, name)
                    .list();
            result.put("success", true);
            result.put("data", list);
            result.put("total", list.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 更新拓扑类型
     * PUT /topologyType
     */
    @PutMapping
    public Map<String, Object> updateTopologyType(@RequestBody TopologyType topologyType) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (topologyType.getTypeId() == null) {
                result.put("success", false);
                result.put("message", "更新失败: ID不能为空");
                return result;
            }

            boolean success = topologyTypeService.updateById(topologyType);
            result.put("success", success);
            result.put("message", success ? "更新成功" : "更新失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 根据ID删除拓扑类型
     * DELETE /topologyType/{id}
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteTopologyType(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = topologyTypeService.removeById(id);
            result.put("success", success);
            result.put("message", success ? "删除成功" : "删除失败，可能ID不存在");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 批量删除拓扑类型
     * DELETE /topologyType/batch
     */
    @DeleteMapping("/batch")
    public Map<String, Object> batchDeleteTopologyType(@RequestBody List<Integer> ids) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = topologyTypeService.removeByIds(ids);
            result.put("success", success);
            result.put("message", success ? "批量删除成功" : "批量删除失败");
            result.put("deletedCount", ids.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量删除失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 插入传感器数据到拓扑类型表（与Monitor程序配合使用）
     * POST /topologyType/sensor
     */
    @PostMapping("/sensor")
    public Map<String, Object> insertSensorData(@RequestParam String sensorName,
                                                @RequestParam Integer sensorValue) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = topologyTypeService.saveSensorData(sensorName, sensorValue);
            result.put("success", success);
            result.put("message", success ? "传感器数据保存成功" : "传感器数据保存失败");
            if (success) {
                result.put("sensorName", sensorName);
                result.put("sensorValue", sensorValue);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "传感器数据保存失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取统计信息
     * GET /topologyType/stats
     */
    @GetMapping("/stats")
    public Map<String, Object> getStatistics() {
        Map<String, Object> result = new HashMap<>();
        try {
            long totalCount = topologyTypeService.count();
            result.put("success", true);
            result.put("totalCount", totalCount);
            result.put("message", "统计信息获取成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "统计信息获取失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 根据值范围查询拓扑类型
     * GET /topologyType/range?min=10&max=100
     */
    @GetMapping("/range")
    public Map<String, Object> getTopologyTypeByValueRange(@RequestParam Integer min,
                                                           @RequestParam Integer max) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<TopologyType> list = topologyTypeService.lambdaQuery()
                    .between(TopologyType::getValue, min, max)
                    .list();
            result.put("success", true);
            result.put("data", list);
            result.put("total", list.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }
}