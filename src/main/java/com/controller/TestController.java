package com.controller;

import com.service.TopologyTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private TopologyTypeService topologyTypeService;

    /**
     * 测试LoRaWan传感器数据插入（使用英文名称）
     */
    @PostMapping("/lorawan")
    public Map<String, Object> testLoRaWanSensorInsert() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 模拟二氧化碳传感器数据
            byte[] co2Data = new byte[] {
                    (byte) 0xFE, // 开始帧
                    (byte) 0x01, // 硬件平台编号
                    (byte) 0x04, // 数据长度
                    (byte) 0xCA, // 传感器类型（二氧化碳）
                    (byte) 0x00, // 模块编号
                    (byte) 0x02, // 高8位
                    (byte) 0x80, // 低8位
                    (byte) 0xEF  // 结束帧
            };

            boolean success1 = topologyTypeService.saveLoRaWanSensorData("CO2", 640, co2Data);

            // 模拟震动传感器数据
            byte[] vibrationData = new byte[] {
                    (byte) 0xFE, // 开始帧
                    (byte) 0x01, // 硬件平台编号
                    (byte) 0x03, // 数据长度
                    (byte) 0xC6, // 传感器类型（震动）
                    (byte) 0x00, // 模块编号
                    (byte) 0x05, // 触发次数
                    (byte) 0xEF  // 结束帧
            };

            boolean success2 = topologyTypeService.saveLoRaWanSensorData("Vibration", 5, vibrationData);

            // 模拟可燃气体传感器数据
            byte[] gasData = new byte[] {
                    (byte) 0xFE, // 开始帧
                    (byte) 0x01, // 硬件平台编号
                    (byte) 0x03, // 数据长度
                    (byte) 0xC5, // 传感器类型（燃气）
                    (byte) 0x00, // 模块编号
                    (byte) 0x02, // 触发次数
                    (byte) 0xEF  // 结束帧
            };

            boolean success3 = topologyTypeService.saveLoRaWanSensorData("Gas", 2, gasData);

            result.put("success", success1 && success2 && success3);
            result.put("message", "LoRaWan传感器测试完成");

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "测试失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 测试百度云传感器数据插入
     */
    @PostMapping("/baidu")
    public Map<String, Object> testBaiduSensorInsert() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 测试紧急按钮
            boolean success1 = topologyTypeService.saveSensorData("PanicButton", 0.0);

            // 测试水浸传感器
            boolean success2 = topologyTypeService.saveSensorData("WaterImmersion", 0.0);

            // 测试温度传感器
            boolean success3 = topologyTypeService.saveSensorData("Temperature", 25.7);

            // 测试湿度传感器
            boolean success4 = topologyTypeService.saveSensorData("Humidity", 58.5);

            // 测试光照强度传感器
            boolean success5 = topologyTypeService.saveSensorData("LLuxSenSor", 581.0);

            result.put("success", success1 && success2 && success3 && success4 && success5);
            result.put("message", "百度云传感器测试完成");

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "测试失败: " + e.getMessage());
        }
        return result;
    }
}