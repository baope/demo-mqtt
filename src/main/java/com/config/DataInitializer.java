package com.config;

import com.entity.ComputingCentral;
import com.entity.Device;
import com.mapper.ComputingCentralMapper;
import com.mapper.DeviceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ComputingCentralMapper computingCentralMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Override
    public void run(String... args) throws Exception {
        initCenters();
        initDevices();
    }

    private void initCenters() {
        List<String> centerNames = Arrays.asList("LoRaWan中心", "WiFi中心", "NB-IoT中心");

        for (String centerName : centerNames) {
            Long count = computingCentralMapper.selectCount(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ComputingCentral>()
                            .eq("central_name", centerName)
            );

            if (count == 0) {
                ComputingCentral center = new ComputingCentral();
                center.setCentralName(centerName);
                // 设置其他字段的默认值
                center.setCpuConsumption(50);
                center.setMemConsumption(50);
                center.setTotal(1000);
                center.setUsed(500);
                center.setFree(500);
                center.setPresent(1);

                computingCentralMapper.insert(center);
                System.out.println("已创建" + centerName + "记录，ID: " + center.getCentralId());
            } else {
                System.out.println(centerName + "记录已存在");
            }
        }
    }

    private void initDevices() {
        List<String> deviceNames = Arrays.asList(
                "CO2传感器", "震动传感器", "可燃气体传感器",
                "紧急按钮", "水浸传感器", "温湿度传感器",
                "红外对射入侵传感器", "吸顶红外入侵传感器", "光照强度传感器"
        );

        for (String deviceName : deviceNames) {
            Long count = deviceMapper.selectCount(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Device>()
                            .eq("device_name", deviceName)
            );

            if (count == 0) {
                Device device = new Device();
                device.setDeviceName(deviceName);
                deviceMapper.insert(device);
                System.out.println("已创建设备记录: " + deviceName + "，ID: " + device.getDeviceId());
            } else {
                System.out.println("设备记录已存在: " + deviceName);
            }
        }
    }
}