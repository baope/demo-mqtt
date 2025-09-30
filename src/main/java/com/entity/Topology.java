package com.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

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
@TableName("topology")
public class Topology implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "topology_id", type = IdType.AUTO)
    private Integer topologyId;


    private Integer edgeId;

    private Integer typeId;

    private LocalDateTime time;


}
