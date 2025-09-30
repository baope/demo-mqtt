package com.monitor;

import com.rimelink.data.common.Connection;
import com.rimelink.data.common.messages.UplinkMessage;
import com.rimelink.data.mqtt.Client;
import com.service.TopologyTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RowaMqttMonitor1 implements CommandLineRunner {

    @Autowired
    private TopologyTypeService topologyTypeService;

    // 定义设备EUI到传感器名称的映射
    private static final Map<String, String> EUI_SENSOR_MAP = new HashMap<>();
    static {
        EUI_SENSOR_MAP.put("303235346442567e", "CO2");        // CO2传感器
        EUI_SENSOR_MAP.put("3032353464416f6e", "Vibration");  // 震动传感器
        EUI_SENSOR_MAP.put("303235346442527d", "Gas");        // 可燃气体传感器
    }

    @Override
    public void run(String... args) throws Exception {
        // 在新线程中启动MQTT监控，避免阻塞Spring主线程
        Thread monitorThread = new Thread(this::startMqttClient);
        monitorThread.setDaemon(true);
        monitorThread.start();
        System.out.println("MQTT监控线程已启动...");
    }

//    public void saveTopology(String name, Integer value,)

    private void startMqttClient() {
        try {
            // 创建MQTT客户端
            Client client = new Client("lorawan.timeddd.com:1883", "36", "");

            // 设置消息接收回调
            client.onMessage((String devEUI, UplinkMessage data) -> {
                try {
                    byte[] received = data.getData();
                    String hexData = bytesToHex(received);
                    System.out.println("收到数据 - 设备EUI: " + devEUI + ", 原始数据: " + hexData);

                    // 根据设备EUI获取传感器名称
                    String sensorName = EUI_SENSOR_MAP.get(devEUI);
                    if (sensorName == null) {
                        System.out.println("未知设备EUI: " + devEUI);
                        return;
                    }

                    // 解析传感器数据
                    Integer sensorValue = null;
                    switch(sensorName) {
                        case "CO2":
                            sensorValue = parseCO2Concentration(received);
                            System.out.println("二氧化碳浓度: " + sensorValue + " ppm");
                            break;
                        case "Vibration":
                            sensorValue = parseTriggerCount(received);
                            System.out.println("震动传感器触发次数: " + sensorValue + " 次");
                            break;
                        case "Gas":
                            sensorValue = parseTriggerCount(received);
                            System.out.println("可燃气体传感器触发次数: " + sensorValue + " 次");
                            break;
                        default:
                            // 其他传感器解析方式
                            sensorValue = parseDefaultValue(received);
                            break;
                    }

                    // 保存到数据库（使用新的方法，包含原始数据）
                    topologyTypeService.saveLoRaWanSensorData(sensorName, sensorValue, received);

                    // 发送响应
                    System.out.println("向设备发送LED控制指令");
                    client.send(devEUI, "led".getBytes(), 1);

                } catch (Exception ex) {
                    System.err.println("处理传感器数据时发生错误: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });

            // 设置错误处理回调
            client.onError((Throwable error) -> {
                System.err.println("MQTT客户端错误: " + error.getMessage());
                error.printStackTrace();
            });

            // 设置连接成功回调
            client.onConnected((Connection _client) -> {
                System.out.println("MQTT客户端连接成功!");
            });

            // 启动客户端
            client.start();
            System.out.println("MQTT客户端启动完成，等待接收数据...");

        } catch (Exception e) {
            System.err.println("MQTT客户端启动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 添加一个默认的解析方法
    private int parseDefaultValue(byte[] data) {
        // 默认解析最后一个字节
        if (data.length < 1) {
            return -1;
        }
        return data[data.length - 1] & 0xFF;
    }

    // 解析二氧化碳浓度（保持原有逻辑）
    public static int parseCO2Concentration(byte[] data) {
        if (data.length < 3) {
            System.err.println("数据长度不足，无法解析二氧化碳浓度");
            return -1;
        }
        int highByte = data[data.length - 3] & 0xFF;
        int lowByte = data[data.length - 2] & 0xFF;
        int concentration = (highByte << 8) | lowByte;
        return concentration;
    }

    // 解析触发次数（保持原有逻辑）
    public static int parseTriggerCount(byte[] data) {
        if (data.length < 2) {
            System.err.println("数据长度不足，无法解析触发次数");
            return -1;
        }
        int triggerCount = data[data.length - 2] & 0xFF;
        return triggerCount;
    }

    // 字节数组转十六进制字符串（保持原有逻辑）
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if(hex.length() < 2) sb.append(0);
            sb.append(hex);
            sb.append(" ");
        }
        return sb.toString();
    }
}
