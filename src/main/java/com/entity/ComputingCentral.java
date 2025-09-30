package com.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * <p>
 * 
 * </p>
 *
 * @author cwj
 * @since 2025-09-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("computingCentral")
public class ComputingCentral implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Random RANDOM = new Random();

    @TableId(value = "central_id", type = IdType.AUTO)
    private Integer centralId;

    private String centralName;

    private Integer cpuConsumption; //30-70中的随机数

    private Integer memConsumption; //30-70中的随机数

    private Integer total;//30-70中的随机数

    private Integer used;//30-70中的随机数

    private Integer free;//30-70中的随机数

    /**
     * 当前状态 (0:离线, 1:在线, 2:忙碌)
     */
    private Integer present;

    private LocalDateTime time;

    /**
     * 无参构造函数 - 自动生成随机值
     */
    public ComputingCentral() {
        generateRandomValues();
    }

    /**
     * 带名称的构造函数
     */
    public ComputingCentral(String centralName) {
        this.centralName = centralName;
        generateRandomValues();
    }

    /**
     * 生成30-70之间的随机值
     */
    private void generateRandomValues() {
        this.cpuConsumption = generateRandomInRange();
        this.memConsumption = generateRandomInRange();
        this.total = generateRandomInRange() * 10; // 放大10倍作为总量
        this.used = (int) (this.total * (this.cpuConsumption / 100.0));
        this.free = this.total - this.used;
        this.present = RANDOM.nextInt(3); // 0,1,2随机状态
        this.time = LocalDateTime.now();
    }

    /**
     * 生成30-70之间的随机整数
     */
    private int generateRandomInRange() {
        return RANDOM.nextInt(41) + 30; // 30-70 (包含30和70)
    }

    /**
     * 手动更新资源数据（重新生成随机值）
     */
    public void updateResourceData() {
        generateRandomValues();
    }

    /**
     * 获取资源使用率百分比
     */
    public Double getUsagePercentage() {
        if (total == null || total == 0) {
            return 0.0;
        }
        return (used.doubleValue() / total.doubleValue()) * 100;
    }

    /**
     * 检查资源是否紧张（使用率超过60%）
     */
    public Boolean isResourceTense() {
        return getUsagePercentage() > 60.0;
    }

    /**
     * 检查是否在线
     */
    public Boolean isOnline() {
        return present != null && present == 1;
    }

    // 在ComputingCentral类中添加以下方法：

    public LocalDateTime getRecordTime() {
        return time;
    }

    public void setRecordTime(LocalDateTime recordTime) {
        this.time = recordTime;
    }

}
