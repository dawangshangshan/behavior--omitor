package com.dawang.monitor.infrastructure.dao;

import com.dawang.monitor.infrastructure.po.MonitorDataMapNodeLink;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IMonitorDataMapNodeLinkDao {
    List<MonitorDataMapNodeLink> queryMonitorNodeListConfigByMonitorId(String monitorId);
    void deleteLinkFromByMonitorId(String monitorId);

    void insert(MonitorDataMapNodeLink monitorDataMapNodeLinkReq);
}
