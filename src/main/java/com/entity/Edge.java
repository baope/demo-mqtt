package com.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
@TableName("edge")
public class Edge implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "edge_id", type = IdType.AUTO)
    private Integer edgeId;

    /**
     * computingCentral_id
     */
    private Integer fromCentral;

    /**
     * topology_id
     */
    private Integer toDevice;


}
