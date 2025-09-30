package com.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

    // 传感器到中心的映射关系
    private static final Map<String, String> SENSOR_CENTER_MAP = new HashMap<>();
    static {
        // 平台1的传感器映射（无后缀）
        SENSOR_CENTER_MAP.put("CO2", "LoRaWan中心");
        SENSOR_CENTER_MAP.put("Vibration", "LoRaWan中心");
        SENSOR_CENTER_MAP.put("Gas", "LoRaWan中心");

        // 平台2的传感器映射（带_1后缀）
        SENSOR_CENTER_MAP.put("CO2_1", "LoRaWan中心1");
        SENSOR_CENTER_MAP.put("Vibration_1", "LoRaWan中心1");
        SENSOR_CENTER_MAP.put("Gas_1", "LoRaWan中心1");

        // 百度平台1的传感器
        SENSOR_CENTER_MAP.put("PanicButton", "WiFi中心");
        SENSOR_CENTER_MAP.put("WaterImmersion", "WiFi中心");
        SENSOR_CENTER_MAP.put("Temperature", "WiFi中心");
        SENSOR_CENTER_MAP.put("Humidity", "WiFi中心");

        // 百度平台2的传感器
        SENSOR_CENTER_MAP.put("PanicButton_1", "WiFi中心1");
        SENSOR_CENTER_MAP.put("WaterImmersion_1", "WiFi中心1");
        SENSOR_CENTER_MAP.put("Temperature_1", "WiFi中心1");
        SENSOR_CENTER_MAP.put("Humidity_1", "WiFi中心1");

        // NB-IoT中心的传感器（英文名称）
        SENSOR_CENTER_MAP.put("InfraredIntrusion", "NB-IoT中心");
        SENSOR_CENTER_MAP.put("CeilingInfrared", "NB-IoT中心");
        SENSOR_CENTER_MAP.put("LLuxSenSor", "NB-IoT中心");

        SENSOR_CENTER_MAP.put("InfraredIntrusion_1", "NB-IoT中心1");
        SENSOR_CENTER_MAP.put("CeilingInfrared_1", "NB-IoT中心1");
        SENSOR_CENTER_MAP.put("LLuxSenSor_1", "NB-IoT中心1");
    }

    // 传感器到协议名称的映射
    private static final Map<String, String> SENSOR_PROTOCOL_MAP = new HashMap<>();
    static {
        SENSOR_PROTOCOL_MAP.put("CO2", "二氧化碳传感器协议");
        SENSOR_PROTOCOL_MAP.put("Vibration", "震动传感器协议");
        SENSOR_PROTOCOL_MAP.put("Gas", "可燃气体传感器协议");
    }

    // 传感器解析规则
    private static final Map<String, String> PROTOCOL_RULES = new HashMap<>();
    static {
        PROTOCOL_RULES.put("CO2",
                "开始帧: FE, 结束帧: EF。开始帧后面的字节为硬件平台编号，第三个字节为数据长度，后面为有效数据。\n" +
                        "二氧化碳传感器数据格式：第一个数据字节是 0xCA（传感器类型），第二个数据字节 00（模块编号），" +
                        "第三和第四个数据字节为二氧化碳数据（16位，高8位在前，低8位在后）");

        PROTOCOL_RULES.put("Vibration",
                "开始帧: FE, 结束帧: EF。开始帧后面的字节为硬件平台编号，第三个字节为数据长度，后面为有效数据。\n" +
                        "震动传感器数据格式：第一个数据字节是 0xC6（传感器类型），第二个数据字节 00（模块编号），" +
                        "第三个数据字节代表单位时间内传感器触发的次数");

        PROTOCOL_RULES.put("Gas",
                "开始帧: FE, 结束帧: EF。开始帧后面的字节为硬件平台编号，第三个字节为数据长度，后面为有效数据。\n" +
                        "可燃气体传感器数据格式：第一个数据字节是 0xC5（传感器类型），第二个数据字节 00（模块编号），" +
                        "第三个数据字节代表单位时间内传感器触发的次数");
    }

    @Override
    @Transactional
    public boolean saveSensorData(String sensorName, Integer sensorValue) {
        return saveSensorData(sensorName, sensorValue != null ? sensorValue.doubleValue() : null);
    }

    @Override
    @Transactional
    public boolean saveSensorData(String sensorName, Double sensorValue) {
        try {
            System.out.println("开始保存传感器数据: " + sensorName + " = " + sensorValue);

            // 1. 插入topologyType表
            TopologyType topologyType = new TopologyType();
            topologyType.setName(sensorName);
            topologyType.setValue(sensorValue);
            boolean typeSaved = this.save(topologyType);

            if (!typeSaved) {
                System.err.println("插入topologyType表失败");
                return false;
            }

            System.out.println("topologyType插入成功，typeId: " + topologyType.getTypeId());

            // 2. 根据传感器名称获取对应的中心名称
            String centerName = SENSOR_CENTER_MAP.get(sensorName);
            if (centerName == null) {
                throw new RuntimeException("未知的传感器类型: " + sensorName);
            }

            // 3. 获取中心的ID
            Integer centerId = edgeService.getLatestCentralIdByName(centerName);
            if (centerId == null) {
                throw new RuntimeException("未找到中心: " + centerName);
            }

            System.out.println("获取到中心ID: " + centerId + " for " + centerName);

            // 4. 根据传感器名称获取设备ID
            Integer deviceId = getDeviceIdBySensorName(sensorName);
            if (deviceId == null) {
                throw new RuntimeException("未找到设备: " + sensorName);
            }

            System.out.println("获取到设备ID: " + deviceId + " for " + sensorName);

            // 5. 插入edge表，连接中心和设备
            Integer edgeId = edgeService.insertEdge(centerId, deviceId);
            if (edgeId == null) {
                throw new RuntimeException("插入edge表失败");
            }

            System.out.println("edge插入成功，edgeId: " + edgeId);

            // 6. 插入topology表（连接edge表和topologyType表）
            Integer topologyId = insertTopologyRecord(edgeId, topologyType.getTypeId());
            if (topologyId == null) {
                throw new RuntimeException("插入topology表失败");
            }

            System.out.println("topology插入成功，topologyId: " + topologyId);

            System.out.println("✅ 传感器数据保存成功: " + sensorName + " = " + sensorValue);
            System.out.println("📊 生成记录 - topologyTypeId: " + topologyType.getTypeId() +
                    ", deviceId: " + deviceId + ", edgeId: " + edgeId +
                    ", topologyId: " + topologyId);

            return true;

        } catch (Exception e) {
            System.err.println("❌ 保存传感器数据时发生错误: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("数据保存失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean saveLoRaWanSensorData(String sensorName, Integer sensorValue, byte[] rawData) {
        try {
            System.out.println("开始保存LoRaWan传感器数据: " + sensorName + " = " + sensorValue);

            // 1. 插入topologyType表
            TopologyType topologyType = new TopologyType();
            topologyType.setName(sensorName);
            topologyType.setValue(sensorValue.doubleValue());
            boolean typeSaved = this.save(topologyType);

            if (!typeSaved) {
                System.err.println("插入topologyType表失败");
                return false;
            }

            System.out.println("topologyType插入成功，typeId: " + topologyType.getTypeId());

            // 2. 根据传感器名称获取对应的中心名称
            String centerName = SENSOR_CENTER_MAP.get(sensorName);
            if (centerName == null) {
                throw new RuntimeException("未知的传感器类型: " + sensorName);
            }

            // 3. 获取中心的ID
            Integer centerId = edgeService.getLatestCentralIdByName(centerName);
            if (centerId == null) {
                throw new RuntimeException("未找到中心: " + centerName);
            }

            System.out.println("获取到中心ID: " + centerId + " for " + centerName);

            // 4. 根据传感器名称获取设备ID
            Integer deviceId = getDeviceIdBySensorName(sensorName);
            if (deviceId == null) {
                throw new RuntimeException("未找到设备: " + sensorName);
            }

            System.out.println("获取到设备ID: " + deviceId + " for " + sensorName);

            // 5. 插入edge表，连接中心和设备
            Integer edgeId = edgeService.insertEdge(centerId, deviceId);
            if (edgeId == null) {
                throw new RuntimeException("插入edge表失败");
            }

            System.out.println("edge插入成功，edgeId: " + edgeId);

            // 6. 插入topology表（连接edge表和topologyType表）
            Integer topologyId = insertTopologyRecord(edgeId, topologyType.getTypeId());
            if (topologyId == null) {
                throw new RuntimeException("插入topology表失败");
            }

            System.out.println("topology插入成功，topologyId: " + topologyId);

            // 7. 解析原始数据并插入protocol和message表
            boolean protocolSaved = insertProtocolAndMessage(sensorName, rawData);
            if (!protocolSaved) {
                throw new RuntimeException("插入protocol和message表失败");
            }

            System.out.println("protocol和message插入成功");

            System.out.println("✅ LoRaWan传感器数据保存成功: " + sensorName + " = " + sensorValue);
            System.out.println("📊 生成记录 - topologyTypeId: " + topologyType.getTypeId() +
                    ", deviceId: " + deviceId + ", edgeId: " + edgeId +
                    ", topologyId: " + topologyId);

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
    private boolean insertProtocolAndMessage(String sensorName, byte[] rawData) {
        try {
            // 1. 解析原始数据生成message内容
            String messageContent = parseRawDataToMessage(rawData, sensorName);

            // 2. 插入message表
            Integer messageId = messageService.insertMessage(messageContent);
            if (messageId == null) {
                return false;
            }

            // 3. 获取协议名称和规则
            String protocolName = SENSOR_PROTOCOL_MAP.get(sensorName);
            String protocolRule = PROTOCOL_RULES.get(sensorName);

            if (protocolName == null || protocolRule == null) {
                System.err.println("未找到传感器对应的协议信息: " + sensorName);
                return false;
            }

            // 4. 插入protocol表
            Integer protocolId = protocolService.insertProtocol(protocolName, protocolRule, messageId);
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
    private String parseRawDataToMessage(byte[] rawData, String sensorName) {
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

        messageBuilder.append("开始帧: ").append(String.format("%02X", startFrame)).append("\n");
        messageBuilder.append("硬件平台编号: ").append(String.format("%02X", hardwarePlatform)).append("\n");
        messageBuilder.append("数据长度: ").append(String.format("%02X", dataLength)).append("\n");

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
    public static String bytesToHex(byte[] bytes) {
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