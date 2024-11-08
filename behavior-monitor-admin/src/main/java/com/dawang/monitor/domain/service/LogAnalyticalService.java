package com.dawang.monitor.domain.service;

import com.alibaba.fastjson.JSONObject;
import com.dawang.monitor.domain.model.entity.MonitorDataEntity;
import com.dawang.monitor.domain.model.entity.MonitorDataMapEntity;
import com.dawang.monitor.domain.model.entity.MonitorFlowDesignerEntity;
import com.dawang.monitor.domain.model.valobj.GatherNodeExpressionVO;
import com.dawang.monitor.domain.model.valobj.MonitorTreeConfigVO;
import com.dawang.monitor.domain.repository.IMonitorRepository;
import com.dawang.monitor.types.Constants;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class LogAnalyticalService implements ILogAnalyticalService{
    @Resource
    private IMonitorRepository repository;
    @Override
    /**
     * 执行日志分析
     * 该方法根据系统名、类名、方法名查询到监控表达式配置，并根据这些配置对日志列表进行解析
     *
     * @param systemName 系统名称，用于查询监控表达式
     * @param className 类名称，与系统名称一起用于精确查询监控表达式
     * @param methodName 方法名称，进一步精确查询监控表达式
     * @param logList 日志列表，包含需要分析的日志信息
     * @throws OgnlException 当使用OGNL表达式解析日志时发生错误
     */
    public void doAnalytical(String systemName, String className, String methodName, List<String> logList) throws OgnlException {
        // 根据系统名、类名、方法名查询监控表达式配置
        List<GatherNodeExpressionVO> gatherNodeExpressionVOS = repository.queryGatherNodeExpressionVO(systemName,className,methodName);

        // 如果没有查询到监控表达式配置，则直接返回
        if(null==gatherNodeExpressionVOS||gatherNodeExpressionVOS.isEmpty()){
            return;
        }

        // 遍历查询到的监控表达式配置
        for(GatherNodeExpressionVO gatherNodeExpressionVO:gatherNodeExpressionVOS){
            // 查询监控项名称
            String monitorName=repository.queryMonitorNameBYMonitorId(gatherNodeExpressionVO.getMonitorId());

            // 获取当前监控表达式配置的所有字段
            List<GatherNodeExpressionVO.Filed>fileds=gatherNodeExpressionVO.getFileds();

            // 遍历所有字段，解析日志
            for(GatherNodeExpressionVO.Filed filed :fileds){
                // 获取当前字段的日志索引
                Integer logIndex = filed.getLogIndex();

                // 获取第一条日志的名称，用于判断是否与当前字段的日志名称匹配
                String logName = logList.get(0);

                // 如果日志名称匹配，则跳过当前循环，继续下一次循环
                if(logName.equals(filed.getLogName())){
                    continue;
                }

                // 初始化属性值字符串
                String attributeValue="";

                // 根据日志索引获取日志内容
                String logStr = logList.get(logIndex);

                // 如果当前字段的日志类型为"Object"，则使用OGNL表达式解析日志内容
                if("Object".equals(filed.getLogType())){
                    OgnlContext context=new OgnlContext();
                    context.setRoot(JSONObject.parseObject(logStr));
                    Object root = context.getRoot();
                    attributeValue= String.valueOf(Ognl.getValue(filed.getAttributeOgnl(), root));
                }else{
                    // 如果日志类型不是"Object"，则直接使用日志内容作为属性值
                    attributeValue= logStr.trim();

                    // 如果属性值中包含冒号，取冒号后的部分作为属性值
                    if(attributeValue.contains(Constants.COLON)){
                        attributeValue=attributeValue.split(Constants.COLON)[1].trim();
                    }
                }

                MonitorDataEntity monitorDataEntity = MonitorDataEntity.builder()
                        .monitorId(gatherNodeExpressionVO.getMonitorId())
                        .monitorName(monitorName)
                        .monitorNodeId(gatherNodeExpressionVO.getMonitorNodeId())
                        .systemName(systemName)
                        .clazzName(className)
                        .attributeName(filed.getAttributeName())
                        .attributeField(filed.getAttributeField())
                        .attributeValue(attributeValue)
                        .build();

                repository.saveMonitorData(monitorDataEntity);
            }
        }
    }

    @Override
    public List<MonitorDataMapEntity> queryMonitorDataMapEntityList() {
        return repository.queryMonitorDataMapEntityList();
    }

    @Override
    public MonitorTreeConfigVO queryMonitorFlowData(String monitorId) {
        return repository.queryMonitorFlowData(monitorId);
    }

    @Override
    public List<MonitorDataEntity> queryMonitorDataEntityList(MonitorDataEntity monitorDataEntity) {
        return repository.queryMonitorDataEntityList(monitorDataEntity);
    }

    @Override
    public void updateMonitorFlowDesigner(MonitorFlowDesignerEntity monitorFlowDesignerEntity) {
         repository.updateMonitorFlowDesigner(monitorFlowDesignerEntity);
    }

}
