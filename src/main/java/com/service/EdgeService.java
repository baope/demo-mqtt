package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.Edge;

public interface EdgeService extends IService<Edge> {

    /**
     * 插入边数据并返回生成的ID
     */
    Integer insertEdge(Integer fromCentral, Integer toDevice);  // 修改参数名

    /**
     * 根据中心名称获取最新的中心ID
     */
    Integer getLatestCentralIdByName(String centralName);
}