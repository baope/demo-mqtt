package com.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entity.ComputingCentral;
import com.mapper.ComputingCentralMapper;
import com.service.ComputingCentralService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ComputingCentralServiceImpl
        extends ServiceImpl<ComputingCentralMapper, ComputingCentral>
        implements ComputingCentralService {

    @Override
    public boolean insertComputingCentral(String centralName) {
        ComputingCentral central = new ComputingCentral(centralName);
        return this.save(central);
    }

    @Override
    public boolean batchInsertTestData(int count) {
        List<ComputingCentral> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            ComputingCentral central = new ComputingCentral("计算中心-" + i);
            list.add(central);
        }
        return this.saveBatch(list);
    }
}