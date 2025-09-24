package com.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

    @TableId(value = "central_id", type = IdType.AUTO)
    private Integer centralId;

    private String centralName;

    private Integer cpuConsumption;

    private Integer memConsumption;

    private Integer total;

    private Integer used;

    private Integer free;

    private Integer present;

    private LocalDateTime time;


}
