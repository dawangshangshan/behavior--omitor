package com.dawang.monitor.domain.service;

import com.dawang.monitor.domain.model.entity.MonitorDataMapEntity;
import com.dawang.monitor.domain.model.valobj.MonitorTreeConfigVO;
import ognl.OgnlException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ILogAnalyticalService {

    void doAnalytical(String systemName, String className, String methodName, List<String> logList) throws OgnlException;

    List<MonitorDataMapEntity>  queryMonitorDataMapEntityList();

    MonitorTreeConfigVO queryMonitorFlowData(String monitorId);
}
