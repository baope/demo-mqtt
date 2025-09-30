package com.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("device")
public class Device {

    @TableId(value = "device_id", type = IdType.AUTO)
    private Integer deviceId;

    private String deviceName;
}