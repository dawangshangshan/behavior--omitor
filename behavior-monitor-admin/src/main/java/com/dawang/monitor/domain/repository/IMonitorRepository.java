package com.dawang.monitor.domain.repository;

import com.dawang.monitor.domain.model.entity.MonitorDataEntity;
import com.dawang.monitor.domain.model.entity.MonitorDataMapEntity;
import com.dawang.monitor.domain.model.valobj.GatherNodeExpressionVO;
import com.dawang.monitor.domain.model.valobj.MonitorTreeConfigVO;


import java.util.List;

public interface IMonitorRepository {

    List<GatherNodeExpressionVO> queryGatherNodeExpressionVO(String systemName, String className, String methodName);

    String queryMonitorNameBYMonitorId(String monitorId);

    void saveMonitorData(MonitorDataEntity monitorDataEntity);

    List<MonitorDataMapEntity> queryMonitorDataMapEntityList();

    MonitorTreeConfigVO queryMonitorFlowData(String monitorId);
}
