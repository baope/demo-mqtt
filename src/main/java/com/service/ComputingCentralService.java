package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.ComputingCentral;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author cwj
 * @since 2025-09-24
 */
public interface ComputingCentralService extends IService<ComputingCentral> {
    /**
     * 插入计算中心数据（带随机值）
     */
    boolean insertComputingCentral(String centralName);

    /**
     * 批量插入测试数据
     */
    boolean batchInsertTestData(int count);

}
