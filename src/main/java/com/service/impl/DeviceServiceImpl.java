package com.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entity.Device;
import com.mapper.DeviceMapper;
import com.service.DeviceService;
import org.springframework.stereotype.Service;

@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device>
        implements DeviceService {

    @Override
    public Integer getDeviceIdByName(String deviceName) {
        Device device = this.getOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Device>()
                        .eq("device_name", deviceName)
        );
        return device != null ? device.getDeviceId() : null;
    }
}