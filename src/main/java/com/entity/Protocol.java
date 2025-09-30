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
@TableName("protocol")
public class Protocol implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "protocol_id", type = IdType.AUTO)
    private Integer protocolId;

    private String protocolName;

    private String value;

    private Integer messageId;
}
