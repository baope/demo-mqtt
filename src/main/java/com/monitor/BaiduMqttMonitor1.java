package com.monitor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.TopologyTypeService;
import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BaiduMqttMonitor1 implements CommandLineRunner {

    @Autowired
    private TopologyTypeService topologyTypeService;

    // 传感器名称映射（英文名称）
    private static final String EMERGENCY_BUTTON = "PanicButton";
    private static final String WATER_LEAK_SENSOR = "WaterImmersion";
    private static final String TEMPERATURE_SENSOR = "Temperature";
    private static final String HUMIDITY_SENSOR = "Humidity";
    private static final String INFRARED_INTRUSION_SENSOR = "InfraredIntrusion";
    private static final String CEILING_INFRARED_SENSOR = "CeilingInfrared";
    private static final String LIGHT_INTENSITY_SENSOR = "LLuxSenSor";

//    @Override
    public void run(String... args) throws Exception {
        // 在新线程中启动百度MQTT监控
        Thread monitorThread = new Thread(this::startBaiduMqttClient);
        monitorThread.setDaemon(true);
        monitorThread.start();
        System.out.println("百度MQTT监控线程已启动...");
    }

    private void startBaiduMqttClient() {
        String broker = "tcp://adtwuzp.iot.gz.baidubce.com";
        String clientId = "demo_client_" + System.currentTimeMillis();
        String topic = "$iot/industry-IOT010/user/update";
        int subQos = 0;

        try {
            MqttClient client = new MqttClient(broker, clientId);
            MqttConnectionOptions options = new MqttConnectionOptions();
            options.setUserName("thingidp@adtwuzp|industry-IOT010|0|MD5");
            options.setPassword("".getBytes());
            options.setAutomaticReconnect(true);
            options.setCleanStart(true);

            client.setCallback(new MqttCallback() {
                public void connectComplete(boolean reconnect, String serverURI) {
                    System.out.println("已连接到百度云MQTT: " + serverURI);
                }

                public void disconnected(MqttDisconnectResponse disconnectResponse) {
                    System.out.println("与百度云MQTT断开连接: " +
                            (disconnectResponse != null ? disconnectResponse.getReasonString() : "未知原因"));

                    // 尝试重新连接
                    try {
                        Thread.sleep(5000);
                        if (!client.isConnected()) {
                            client.connect(options);
                            client.subscribe(topic, subQos);
                        }
                    } catch (Exception e) {
                        System.err.println("重连失败: " + e.getMessage());
                    }
                }

                public void deliveryComplete(IMqttToken token) {
                    System.out.println("消息发送完成: " + token.isComplete());
                }

                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String payload = new String(message.getPayload());

                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode root = mapper.readTree(payload);

                        if (root.has("reported")) {
                            JsonNode reported = root.get("reported");
                            processReportedData(reported);
                        }
                    } catch (Exception e) {
                        System.err.println("解析百度云MQTT消息出错: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                public void mqttErrorOccurred(MqttException exception) {
                    System.err.println("百度云MQTT错误: " + exception.getMessage());
                    exception.printStackTrace();
                }

                public void authPacketArrived(int reasonCode, MqttProperties properties) {
                    System.out.println("百度云MQTT认证包到达");
                }
            });

            client.connect(options);
            client.subscribe(topic, subQos);
            System.out.println("百度云MQTT客户端启动完成，等待接收数据...");

            // 保持运行，不主动断开
            while (true) {
                try {
                    Thread.sleep(60000);
                    if (!client.isConnected()) {
                        System.out.println("检测到连接断开，尝试重新连接...");
                        client.connect(options);
                        client.subscribe(topic, subQos);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

        } catch (MqttException e) {
            System.err.println("百度云MQTT客户端启动失败: " + e.getMessage());
            e.printStackTrace();

            // 启动失败后等待一段时间重试
            try {
                Thread.sleep(30000);
                startBaiduMqttClient();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 处理reported数据
     */
    private void processReportedData(JsonNode reported) {
        reported.fieldNames().forEachRemaining(key -> {
            JsonNode node = reported.get(key);
            processSensorData(key, node);
        });
    }

    /**
     * 处理传感器数据 - 直接存储原始值
     */
    private void processSensorData(String sensorKey, JsonNode node) {
        try {
            String sensorName = getSensorNameByKey(sensorKey);

            if (sensorName == null) {
                System.out.println("未知传感器类型: " + sensorKey);
                return;
            }

            Double sensorValue = null;

            if (node.isTextual()) {
                // 文本型（状态数据）
                String status = node.asText();
                int code = "normal".equalsIgnoreCase(status.replace("!", "")) ? 0 : 1;
                sensorValue = (double) code;
//                System.out.println("传感器: " + sensorName + " (" + sensorKey + "), 状态: " + status + ", Code: " + code);
            } else if (node.isNumber()) {
                // 数值型（温度、湿度、光照等）- 直接存储原始值
                sensorValue = node.asDouble();
//                System.out.println("传感器: " + sensorName + " (" + sensorKey + "), 数值: " + sensorValue);
            } else {
//                System.out.println("传感器: " + sensorName + " (" + sensorKey + "), 不支持的类型: " + node.toString());
                return;
            }

            // 保存到数据库
            if (sensorValue != null) {
                boolean success = topologyTypeService.saveSensorData(sensorName+"_1", sensorValue);
                if (success) {
//                    System.out.println("✅ 百度云传感器数据保存成功: " + sensorName + " = " + sensorValue);
                } else {
                    System.err.println("❌ 百度云传感器数据保存失败: " + sensorName + " = " + sensorValue);
                }
            }

        } catch (Exception e) {
            System.err.println("处理传感器数据时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 根据JSON键名获取传感器英文名称
     */
    private String getSensorNameByKey(String key) {
        // 根据实际MQTT消息中的键名映射到传感器英文名称
        switch (key) {
            case "WaterImmersion":
                return WATER_LEAK_SENSOR;
            case "Temperature":
                return TEMPERATURE_SENSOR;
            case "Humidity":
                return HUMIDITY_SENSOR;
            case "LLuxSenSor":
                return LIGHT_INTENSITY_SENSOR;
            case "PanicButton":
                return EMERGENCY_BUTTON;
            case "InfraredIntrusion":
                return INFRARED_INTRUSION_SENSOR;
            case "CeilingInfrared":
                return CEILING_INFRARED_SENSOR;
            default:
                System.out.println("未知传感器键名: " + key);
                return null;
        }
    }
}
