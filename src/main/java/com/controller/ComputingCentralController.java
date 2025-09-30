package com.controller;

import com.entity.ComputingCentral;
import com.service.ComputingCentralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 计算中心前端控制器
 * </p>
 *
 * @author cwj
 * @since 2025-09-24
 */
@RestController
@RequestMapping("/computingCentral")
public class ComputingCentralController {

    @Autowired
    private ComputingCentralService computingCentralService;

    /**
     * 插入计算中心数据
     * POST /computingCentral/insert
     */
    @PostMapping("/insert")
    public Boolean insertComputingCentral(@RequestParam String centralName) {
        // 创建实体对象并保存
        ComputingCentral central = new ComputingCentral();
        central.setCentralName(centralName);
        // 其他字段会在实体类构造函数中自动生成随机值
        return computingCentralService.save(central);
    }

    /**
     * 插入计算中心数据（JSON方式）
     * POST /computingCentral
     */
    @PostMapping
    public Boolean insertComputingCentral(@RequestBody ComputingCentral computingCentral) {
        // 如果前端传递完整对象，直接保存
        if (computingCentral.getRecordTime() == null) {
            computingCentral.setRecordTime(java.time.LocalDateTime.now());
        }
        return computingCentralService.save(computingCentral);
    }

    /**
     * 根据ID查询计算中心
     * GET /computingCentral/{id}
     */
    @GetMapping("/{id}")
    public ComputingCentral getById(@PathVariable Integer id) {
        return computingCentralService.getById(id);
    }

    /**
     * 查询所有计算中心数据
     * GET /computingCentral/list
     */
    @GetMapping("/list")
    public List<ComputingCentral> getAllComputingCentrals() {
        return computingCentralService.list();
    }

    /**
     * 更新计算中心数据
     * PUT /computingCentral
     */
    @PutMapping
    public Boolean updateComputingCentral(@RequestBody ComputingCentral computingCentral) {
        return computingCentralService.updateById(computingCentral);
    }

    /**
     * 根据ID删除计算中心
     * DELETE /computingCentral/{id}
     */
    @DeleteMapping("/{id}")
    public Boolean deleteComputingCentral(@PathVariable Integer id) {
        return computingCentralService.removeById(id);
    }

    /**
     * 批量插入测试数据
     * POST /computingCentral/batchInsert?count=5
     */
    @PostMapping("/batchInsert")
    public Boolean batchInsertTestData(@RequestParam(defaultValue = "5") Integer count) {
        for (int i = 1; i <= count; i++) {
            ComputingCentral central = new ComputingCentral();
            central.setCentralName("测试计算中心-" + i);
            computingCentralService.save(central);
        }
        return true;
    }

    /**
     * 获取资源使用率高的计算中心（CPU使用率>60%）
     * GET /computingCentral/highUsage
     */
    @GetMapping("/highUsage")
    public List<ComputingCentral> getHighUsageCenters() {
        List<ComputingCentral> allCenters = computingCentralService.list();
        // 过滤出CPU使用率高于60%的计算中心
        return allCenters.stream()
                .filter(center -> center.getCpuConsumption() != null && center.getCpuConsumption() > 60)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 统计计算中心数量
     * GET /computingCentral/count
     */
    @GetMapping("/count")
    public Long getComputingCentralCount() {
        return computingCentralService.count();
    }
}