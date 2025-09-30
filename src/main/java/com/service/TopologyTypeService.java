package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.TopologyType;

public interface TopologyTypeService extends IService<TopologyType> {

    /**
     * 保存传感器数据到topologyType表 (Double类型)
     */
    boolean saveSensorData(String sensorName, Double sensorValue);

    /**
     * 保存传感器数据到topologyType表 (Integer类型)
     */
    boolean saveSensorData(String sensorName, Integer sensorValue);

    /**
     * 保存LoRaWan传感器数据（包含原始数据和协议信息）
     */
    boolean saveLoRaWanSensorData(String sensorName, Integer sensorValue, byte[] rawData);
}