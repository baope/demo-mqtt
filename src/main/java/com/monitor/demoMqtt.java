package com.monitor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

public class demoMqtt {
    public static void main(String[] args) {
        String broker = "tcp://adtwuzp.iot.gz.baidubce.com";
        String clientId = "demo_client";
        String topic = "$iot/industry-IOT013/user/update";
        int subQos = 0;

        try {
            MqttClient client = new MqttClient(broker, clientId);
            MqttConnectionOptions options = new MqttConnectionOptions();
            options.setUserName("thingidp@adtwuzp|industry-IOT013|0|MD5");
            options.setPassword("57077824e2502e032d24164aad247fa0".getBytes());

            client.setCallback(new MqttCallback() {
                public void connectComplete(boolean reconnect, String serverURI) {
                    System.out.println("connected to: " + serverURI);
                }

                public void disconnected(MqttDisconnectResponse disconnectResponse) {
                    System.out.println("disconnected: " + disconnectResponse.getReasonString());
                }

                public void deliveryComplete(IMqttToken token) {
                    System.out.println("deliveryComplete: " + token.isComplete());
                }

                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String payload = new String(message.getPayload());
                    System.out.println("message content: " + payload);

                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode root = mapper.readTree(payload);

                        if (root.has("reported")) {
                            JsonNode reported = root.get("reported");

                            reported.fieldNames().forEachRemaining(key -> {
                                JsonNode node = reported.get(key);

                                if (node.isTextual()) {
                                    // 文本型（状态数据）
                                    String status = node.asText();
                                    int code = "normal".equalsIgnoreCase(status.replace("!", "")) ? 0 : 1;
                                    System.out.println("Sensor: " + key + ", Status: " + status + ", Code: " + code);
                                } else if (node.isNumber()) {
                                    // 数值型（温度、湿度、光照等）
                                    System.out.println("Sensor: " + key + ", Value: " + node.asDouble());
                                } else {
                                    System.out.println("Sensor: " + key + ", Unsupported type: " + node.toString());
                                }
                            });
                        }
                    } catch (Exception e) {
                        System.out.println("解析 message content 出错: " + e.getMessage());
                    }
                }

                public void mqttErrorOccurred(MqttException exception) {
                    System.out.println("mqttErrorOccurred: " + exception.getMessage());
                }

                public void authPacketArrived(int reasonCode, MqttProperties properties) {
                    System.out.println("authPacketArrived");
                }
            });

            client.connect(options);
            client.subscribe(topic, subQos);

            Thread.sleep(6000000); // 保持 1 分钟
            client.disconnect();
            client.close();

        } catch (MqttException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
