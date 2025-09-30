package com.vo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.entity.Edge;
import com.entity.Topology;
import com.entity.TopologyType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TopologyVo {
    @TableId(value = "topology_id", type = IdType.AUTO)
    private Integer topologyId;

    private String name;

    private Edge edge;

    private TopologyType type;

    private LocalDateTime time;

    public TopologyVo(Topology topology) {
        this.topologyId = topology.getTopologyId();
        this.time = topology.getTime();
    }
    public TopologyVo() {}

}
