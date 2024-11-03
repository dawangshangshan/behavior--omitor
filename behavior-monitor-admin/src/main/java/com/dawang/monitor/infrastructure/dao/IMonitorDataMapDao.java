package com.dawang.monitor.infrastructure.dao;

import com.dawang.monitor.domain.model.entity.MonitorDataMapEntity;
import com.dawang.monitor.infrastructure.po.MonitorData;
import com.dawang.monitor.infrastructure.po.MonitorDataMap;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IMonitorDataMapDao {
    String queryMonitorNameBYMonitorId(String monitorId);


    List<MonitorDataMap> queryMonitorDataMapList();
}
