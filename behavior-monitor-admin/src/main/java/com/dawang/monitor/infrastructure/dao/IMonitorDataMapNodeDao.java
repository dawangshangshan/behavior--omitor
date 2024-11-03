package com.dawang.monitor.infrastructure.dao;

import com.dawang.monitor.infrastructure.po.MonitorDataMapNode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IMonitorDataMapNodeDao {
    List<MonitorDataMapNode> queryMonitorDataMapNodeList(MonitorDataMapNode monitorDataMapNode);

    List<MonitorDataMapNode> queryMonitorNodeConfigByMonitorId(String monitorId);

    void updateNodeConfig(MonitorDataMapNode monitorDataMapNodeReq);
}
