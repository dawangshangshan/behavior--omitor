package com.dawang.monitor.infrastructure.dao;

import com.dawang.monitor.infrastructure.po.MonitorData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IMonitorDataDao {
    List<MonitorData>queryMonitorDataList(MonitorData monitorData);
}
