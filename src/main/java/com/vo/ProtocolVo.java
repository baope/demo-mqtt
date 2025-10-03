package com.vo;

import com.entity.Protocol;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProtocolVo {
    public List<ProtocolField> message;
    public String protocol_name;
    public LocalDateTime timestamp;
    public String protocol_value;
    public Double parse_time;
    public boolean expand;
    public Integer _id;
    public ProtocolVo(Protocol protocol) {
        this.protocol_name = protocol.getProtocolName();
        this.timestamp = protocol.getTimestamp();
        this.protocol_value = protocol.getValue();
        this._id = protocol.getProtocolId();
        this.expand = false;
    }

}
