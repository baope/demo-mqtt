package com.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CenterMap;
import com.entity.Topology;
import com.entity.TopologyType;
import com.mapper.TopologyTypeMapper;
import com.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class TopologyTypeServiceImpl extends ServiceImpl<TopologyTypeMapper, TopologyType>
        implements TopologyTypeService {

    @Autowired
    private EdgeService edgeService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private TopologyService topologyService;

    @Autowired
    private ProtocolService protocolService;

    @Autowired
    private MessageService messageService;



    @Override
    @Transactional
    public boolean saveSensorData(String sensorName, Integer sensorValue) {
        return saveSensorData(sensorName, sensorValue != null ? sensorValue.doubleValue() : null);
    }

    @Override
    @Transactional
    public boolean saveSensorData(String sensorName, Double sensorValue) {
        try {
            // 1. 插入topologyType表
            TopologyType topologyType = new TopologyType();
            topologyType.setName(sensorName);
            topologyType.setValue(sensorValue);
            boolean typeSaved = this.save(topologyType);

            if (!typeSaved) {
                System.err.println("插入topologyType表失败");
                return false;
            }

            // 2. 根据传感器名称获取对应的中心名称
            String centerName = CenterMap.SENSOR_CENTER_MAP.get(sensorName);
            if (centerName == null) {
                throw new RuntimeException("未知的传感器类型: " + sensorName);
            }

            // 3. 获取中心的ID
            Integer centerId = edgeService.getLatestCentralIdByName(centerName);
            if (centerId == null) {
                throw new RuntimeException("未找到中心: " + centerName);
            }


            // 4. 根据传感器名称获取设备ID
            Integer deviceId = getDeviceIdBySensorName(sensorName);
            if (deviceId == null) {
                throw new RuntimeException("未找到设备: " + sensorName);
            }


            // 5. 插入edge表，连接中心和设备
            Integer edgeId = edgeService.insertEdge(centerId, deviceId);
            if (edgeId == null) {
                throw new RuntimeException("插入edge表失败");
            }


            // 6. 插入topology表（连接edge表和topologyType表）
            Integer topologyId = insertTopologyRecord(edgeId, topologyType.getTypeId());
            if (topologyId == null) {
                throw new RuntimeException("插入topology表失败");
            }

            System.out.println("✅ 传感器数据保存成功: " + sensorName + " = " + sensorValue);
            return true;

        } catch (Exception e) {
            System.err.println("❌ 保存传感器数据时发生错误: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("数据保存失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean saveLoRaWanSensorData(String sensorName, Integer sensorValue, byte[] rawData,String devEUI) {
        try {

            // 1. 插入topologyType表
            TopologyType topologyType = new TopologyType();
            topologyType.setName(sensorName);
            topologyType.setValue(sensorValue.doubleValue());
            boolean typeSaved = this.save(topologyType);

            if (!typeSaved) {
                System.err.println("插入topologyType表失败");
                return false;
            }


            // 2. 根据传感器名称获取对应的中心名称
            String centerName = CenterMap.SENSOR_CENTER_MAP.get(sensorName);
            if (centerName == null) {
                throw new RuntimeException("未知的传感器类型: " + sensorName);
            }

            // 3. 获取中心的ID
            Integer centerId = edgeService.getLatestCentralIdByName(centerName);
            if (centerId == null) {
                throw new RuntimeException("未找到中心: " + centerName);
            }



            // 4. 根据传感器名称获取设备ID
            Integer deviceId = getDeviceIdBySensorName(sensorName);
            if (deviceId == null) {
                throw new RuntimeException("未找到设备: " + sensorName);
            }


            // 5. 插入edge表，连接中心和设备
            Integer edgeId = edgeService.insertEdge(centerId, deviceId);
            if (edgeId == null) {
                throw new RuntimeException("插入edge表失败");
            }


            // 6. 插入topology表（连接edge表和topologyType表）
            Integer topologyId = insertTopologyRecord(edgeId, topologyType.getTypeId());
            if (topologyId == null) {
                throw new RuntimeException("插入topology表失败");
            }

//            System.out.println("topology插入成功，topologyId: " + topologyId);

            // 7. 解析原始数据并插入protocol和message表
            boolean protocolSaved = insertProtocolAndMessage(sensorName, rawData,devEUI,"RowaLan");
            if (!protocolSaved) {
                throw new RuntimeException("插入protocol和message表失败");
            }


            System.out.println("✅ LoRaWan传感器数据保存成功: " + sensorName + " = " + sensorValue);

            return true;

        } catch (Exception e) {
            System.err.println("❌ 保存LoRaWan传感器数据时发生错误: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("LoRaWan数据保存失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据传感器名称获取设备ID
     */
    private Integer getDeviceIdBySensorName(String sensorName) {
        // 传感器名称到设备名称的映射
        Map<String, String> sensorDeviceMap = new HashMap<>();

        // LoRaWan传感器（英文->中文）
        sensorDeviceMap.put("CO2", "CO2传感器");
        sensorDeviceMap.put("Vibration", "震动传感器");
        sensorDeviceMap.put("Gas", "可燃气体传感器");

        // 英文传感器映射（使用实际接收到的键名）
        sensorDeviceMap.put("PanicButton", "紧急按钮");
        sensorDeviceMap.put("WaterImmersion", "水浸传感器");
        sensorDeviceMap.put("Temperature", "温湿度传感器");
        sensorDeviceMap.put("Humidity", "温湿度传感器");
        sensorDeviceMap.put("InfraredIntrusion", "红外对射入侵传感器");
        sensorDeviceMap.put("CeilingInfrared", "吸顶红外入侵传感器");
        sensorDeviceMap.put("LLuxSenSor", "光照强度传感器");


        sensorDeviceMap.put("CO2_1", "CO2传感器1");
        sensorDeviceMap.put("Vibration_1", "震动传感器1");
        sensorDeviceMap.put("Gas_1", "可燃气体传感器1");
        sensorDeviceMap.put("PanicButton_1", "紧急按钮1");
        sensorDeviceMap.put("WaterImmersion_1", "水浸传感器1");
        sensorDeviceMap.put("Temperature_1", "温湿度传感器1");
        sensorDeviceMap.put("Humidity_1", "温湿度传感器1");
        sensorDeviceMap.put("InfraredIntrusion_1", "红外对射入侵传感器1");
        sensorDeviceMap.put("CeilingInfrared_1", "吸顶红外入侵传感器1");
        sensorDeviceMap.put("LLuxSenSor_1", "光照强度传感器1");

        String deviceName = sensorDeviceMap.get(sensorName);
        if (deviceName == null) {
            System.err.println("未找到传感器对应的设备: " + sensorName);
            return null;
        }

        return deviceService.getDeviceIdByName(deviceName);
    }

    /**
     * 插入topology表记录（连接edge表和topologyType表）
     */
    private Integer insertTopologyRecord(Integer edgeId, Integer typeId) {
        Topology topology = new Topology();
        topology.setEdgeId(edgeId);
        topology.setTypeId(typeId);
        topology.setTime(java.time.LocalDateTime.now());

        boolean saved = topologyService.save(topology);
        return saved ? topology.getTopologyId() : null;
    }

    /**
     * 插入protocol和message表
     */
    private boolean insertProtocolAndMessage(String sensorName, byte[] rawData,String devEUI,String centralName) {
        try {

            String protocolName = CenterMap.SENSOR_PROTOCOL_MAP.get(sensorName);
            if (protocolName == null) {
                System.err.println("未找到传感器对应的协议信息: " + sensorName);
                return false;
            }

            // 1. 解析原始数据生成message内容
            String messageContent = parseRawDataToMessage(rawData, protocolName,devEUI);

            // 2. 插入message表
            Integer messageId = messageService.insertMessage(messageContent);
            if (messageId == null) {
                return false;
            }

            // 3. 获取协议名称和规则
            String hexData = bytesToHex(rawData);

            // 4. 插入protocol表
            Integer protocolId = protocolService.insertProtocol(centralName, hexData, messageId);
            return protocolId != null;

        } catch (Exception e) {
            System.err.println("插入protocol和message表时发生错误: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 解析原始数据生成message内容
     */
    private String parseRawDataToMessage(byte[] rawData, String sensorName,String devEUI) {
        StringBuilder messageBuilder = new StringBuilder();

        // 将字节数组转换为十六进制字符串
        String hexData = bytesToHex(rawData);
        messageBuilder.append("原始数据: ").append(hexData).append("\n");

        // 检查数据长度
        if (rawData.length < 5) {
            messageBuilder.append("数据长度不足，无法解析");
            return messageBuilder.toString();
        }

        // 解析通用字段
        byte startFrame = rawData[0];
        byte hardwarePlatform = rawData[1];
        byte dataLength = rawData[2];

        messageBuilder.append("设备名称: ").append(sensorName).append("\n");
        messageBuilder.append("开始帧: ").append(String.format("%02X", startFrame)).append("\n");
        messageBuilder.append("硬件平台编号: ").append(String.format("%02X", hardwarePlatform)).append("\n");
        messageBuilder.append("数据长度: ").append(String.format("%02X", dataLength)).append("\n");
        messageBuilder.append("设备编号: ").append(devEUI).append("\n");

        // 根据传感器类型解析具体数据
        switch (sensorName) {
            case "CO2":
                parseCO2Data(rawData, messageBuilder);
                break;
            case "Vibration":
                parseVibrationData(rawData, messageBuilder);
                break;
            case "Gas":
                parseGasData(rawData, messageBuilder);
                break;
            default:
                messageBuilder.append("未知传感器类型: ").append(sensorName);
                break;
        }

        // 结束帧
        if (rawData.length > 3) {
            byte endFrame = rawData[rawData.length - 1];
            messageBuilder.append("结束帧: ").append(String.format("%02X", endFrame));
        }

        return messageBuilder.toString();
    }

    /**
     * 解析二氧化碳传感器数据
     */
    private void parseCO2Data(byte[] rawData, StringBuilder messageBuilder) {
        if (rawData.length < 7) {
            messageBuilder.append("二氧化碳传感器数据长度不足");
            return;
        }

        byte sensorType = rawData[3];
        byte moduleId = rawData[4];
        byte highByte = rawData[5];
        byte lowByte = rawData[6];

        int co2Value = ((highByte & 0xFF) << 8) | (lowByte & 0xFF);

        messageBuilder.append("传感器类型: ").append(String.format("%02X", sensorType)).append(" (二氧化碳传感器)\n");
        messageBuilder.append("模块编号: ").append(String.format("%02X", moduleId)).append("\n");
        messageBuilder.append("二氧化碳数据高8位: ").append(String.format("%02X", highByte)).append("\n");
        messageBuilder.append("二氧化碳数据低8位: ").append(String.format("%02X", lowByte)).append("\n");
        messageBuilder.append("二氧化碳浓度值: ").append(co2Value).append(" ppm");
    }

    /**
     * 解析震动传感器数据
     */
    private void parseVibrationData(byte[] rawData, StringBuilder messageBuilder) {
        if (rawData.length < 6) {
            messageBuilder.append("震动传感器数据长度不足");
            return;
        }

        byte sensorType = rawData[3];
        byte moduleId = rawData[4];
        byte triggerCount = rawData[5];

        messageBuilder.append("传感器类型: ").append(String.format("%02X", sensorType)).append(" (震动传感器)\n");
        messageBuilder.append("模块编号: ").append(String.format("%02X", moduleId)).append("\n");
        messageBuilder.append("触发次数: ").append(String.format("%02X", triggerCount)).append(" (").append(triggerCount & 0xFF).append(" 次)");
    }

    /**
     * 解析可燃气体传感器数据
     */
    private void parseGasData(byte[] rawData, StringBuilder messageBuilder) {
        if (rawData.length < 6) {
            messageBuilder.append("可燃气体传感器数据长度不足");
            return;
        }

        byte sensorType = rawData[3];
        byte moduleId = rawData[4];
        byte triggerCount = rawData[5];

        messageBuilder.append("传感器类型: ").append(String.format("%02X", sensorType)).append(" (可燃气体传感器)\n");
        messageBuilder.append("模块编号: ").append(String.format("%02X", moduleId)).append("\n");
        messageBuilder.append("触发次数: ").append(String.format("%02X", triggerCount)).append(" (").append(triggerCount & 0xFF).append(" 次)");
    }

    /**
     * 字节数组转十六进制字符串
     */
    public String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if(hex.length() < 2) sb.append(0);
            sb.append(hex);
            sb.append(" ");
        }
        return sb.toString().trim();
    }
}