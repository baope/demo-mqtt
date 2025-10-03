package com.common;

import java.util.HashMap;
import java.util.Map;

public class CenterMap {
    // 传感器到中心的映射关系

    public static final Map<String, String> SENSOR_CENTER_MAP = new HashMap<>();
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
    public static final Map<String, String> SENSOR_PROTOCOL_MAP = new HashMap<>();
    static {
        SENSOR_PROTOCOL_MAP.put("CO2", "二氧化碳传感器协议");
        SENSOR_PROTOCOL_MAP.put("Vibration", "震动传感器协议");
        SENSOR_PROTOCOL_MAP.put("Gas", "可燃气体传感器协议");

        SENSOR_PROTOCOL_MAP.put("CO2_1", "二氧化碳传感器协议");
        SENSOR_PROTOCOL_MAP.put("Vibration_1", "震动传感器协议");
        SENSOR_PROTOCOL_MAP.put("Gas_1", "可燃气体传感器协议");
    }

    // 传感器解析规则
    public static final Map<String, String> PROTOCOL_RULES = new HashMap<>();
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

        PROTOCOL_RULES.put("CO2_1",
                "开始帧: FE, 结束帧: EF。开始帧后面的字节为硬件平台编号，第三个字节为数据长度，后面为有效数据。\n" +
                        "二氧化碳传感器数据格式：第一个数据字节是 0xCA（传感器类型），第二个数据字节 00（模块编号），" +
                        "第三和第四个数据字节为二氧化碳数据（16位，高8位在前，低8位在后）");

        PROTOCOL_RULES.put("Vibration_1",
                "开始帧: FE, 结束帧: EF。开始帧后面的字节为硬件平台编号，第三个字节为数据长度，后面为有效数据。\n" +
                        "震动传感器数据格式：第一个数据字节是 0xC6（传感器类型），第二个数据字节 00（模块编号），" +
                        "第三个数据字节代表单位时间内传感器触发的次数");

        PROTOCOL_RULES.put("Gas_1",
                "开始帧: FE, 结束帧: EF。开始帧后面的字节为硬件平台编号，第三个字节为数据长度，后面为有效数据。\n" +
                        "可燃气体传感器数据格式：第一个数据字节是 0xC5（传感器类型），第二个数据字节 00（模块编号），" +
                        "第三个数据字节代表单位时间内传感器触发的次数");
    }
}
