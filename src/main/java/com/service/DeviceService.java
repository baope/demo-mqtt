package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.Device;

public interface DeviceService extends IService<Device> {

    /**
     * 根据设备名称获取设备ID
     */
    Integer getDeviceIdByName(String deviceName);
}