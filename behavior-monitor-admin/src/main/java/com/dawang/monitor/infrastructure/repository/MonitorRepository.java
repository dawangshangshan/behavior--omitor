package com.dawang.monitor.infrastructure.repository;

import com.dawang.monitor.domain.model.entity.MonitorDataEntity;
import com.dawang.monitor.domain.model.entity.MonitorDataMapEntity;
import com.dawang.monitor.domain.model.valobj.GatherNodeExpressionVO;
import com.dawang.monitor.domain.model.valobj.MonitorTreeConfigVO;
import com.dawang.monitor.domain.repository.IMonitorRepository;
import com.dawang.monitor.infrastructure.dao.*;
import com.dawang.monitor.infrastructure.po.*;
import com.dawang.monitor.infrastructure.redis.IRedisService;
import com.dawang.monitor.types.Constants;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class MonitorRepository implements IMonitorRepository {
    @Resource
    private IMonitorDataDao monitorDataDao;
    @Resource
    private IMonitorDataMapDao monitorDataMapDao;
    @Resource
    private IMonitorDataMapNodeDao monitorDataMapNodeDao;
    @Resource
    private IMonitorDataMapNodeFieldDao monitorDataMapNodeFieldDao;
    @Resource
    private IMonitorDataMapNodeLinkDao monitorDataMapNodeLinkDao;
    @Resource
    private IRedisService redisService;
    @Resource
    private TransactionTemplate transactionTemplate;


    @Override
    public List<GatherNodeExpressionVO> queryGatherNodeExpressionVO(String systemName, String className, String methodName) {
        MonitorDataMapNode monitorDataMapNodeReq=new MonitorDataMapNode();
        monitorDataMapNodeReq.setGatherSystemName(systemName);
        monitorDataMapNodeReq.setGatherClazzName(className);
        monitorDataMapNodeReq.setGatherMethodName(methodName);
        List<MonitorDataMapNode>monitorDataMapNodes = monitorDataMapNodeDao.queryMonitorDataMapNodeList(monitorDataMapNodeReq);
        if(monitorDataMapNodes.isEmpty())return null;


        List<GatherNodeExpressionVO> gatherNodeExpressionVOS = new ArrayList<>();
        for (MonitorDataMapNode monitorDataMapNodeRes : monitorDataMapNodes) {
            // 2. 查询采集公式
            MonitorDataMapNodeField monitorDataMapNodeFieldReq = new MonitorDataMapNodeField();
            monitorDataMapNodeFieldReq.setMonitorId(monitorDataMapNodeRes.getMonitorId());
            monitorDataMapNodeFieldReq.setMonitorNodeId(monitorDataMapNodeRes.getMonitorNodeId());
            List<MonitorDataMapNodeField> monitorDataMapNodeFieldList = monitorDataMapNodeFieldDao.queryMonitorDataMapNodeList(monitorDataMapNodeFieldReq);

            List<GatherNodeExpressionVO.Filed> fileds = new ArrayList<>();
            for (MonitorDataMapNodeField monitorDataMapNodeField : monitorDataMapNodeFieldList) {
                fileds.add(GatherNodeExpressionVO.Filed.builder()
                        .logName(monitorDataMapNodeField.getLogName())
                        .logIndex(monitorDataMapNodeField.getLogIndex())
                        .logType(monitorDataMapNodeField.getLogType())
                        .attributeField(monitorDataMapNodeField.getAttributeField())
                        .attributeName(monitorDataMapNodeField.getAttributeName())
                        .attributeOgnl(monitorDataMapNodeField.getAttributeOgnl())
                        .build());
            }

            gatherNodeExpressionVOS.add(GatherNodeExpressionVO.builder()
                    .monitorId(monitorDataMapNodeRes.getMonitorId())
                    .monitorNodeId(monitorDataMapNodeRes.getMonitorNodeId())
                    .gatherSystemName(monitorDataMapNodeRes.getGatherSystemName())
                    .gatherClazzName(monitorDataMapNodeRes.getGatherClazzName())
                    .gatherMethodName(monitorDataMapNodeRes.getGatherMethodName())
                    .fileds(fileds)
                    .build());
        }
        return gatherNodeExpressionVOS;
    }

    @Override
    public String queryMonitorNameBYMonitorId(String monitorId) {
        return monitorDataMapDao.queryMonitorNameBYMonitorId(monitorId);
    }

    @Override
    public void saveMonitorData(MonitorDataEntity monitorDataEntity) {
        MonitorData monitorDataReq = MonitorData.builder()
                .monitorId(monitorDataEntity.getMonitorId())
                .monitorName(monitorDataEntity.getMonitorName())
                .monitorNodeId(monitorDataEntity.getMonitorNodeId())
                .systemName(monitorDataEntity.getSystemName())
                .clazzName(monitorDataEntity.getClazzName())
                .methodName(monitorDataEntity.getMethodName())
                .attributeName(monitorDataEntity.getAttributeName())
                .attributeField(monitorDataEntity.getAttributeField())
                .attributeValue(monitorDataEntity.getAttributeValue())
                .build();

        monitorDataDao.insert(monitorDataReq);
        String cacheKey = Constants.RedisKey.monitor_node_data_count_key+monitorDataEntity.getMonitorId()+Constants.SPLIT+monitorDataEntity.getMonitorNodeId();
        redisService.incr(cacheKey);
    }

    @Override
    public List<MonitorDataMapEntity> queryMonitorDataMapEntityList() {
        List<MonitorDataMap> monitorDataMaps = monitorDataMapDao.queryMonitorDataMapList();
        List<MonitorDataMapEntity> monitorDataMapEntities = new ArrayList<>(monitorDataMaps.size());
        for (MonitorDataMap monitorDataMap : monitorDataMaps){
            monitorDataMapEntities.add(MonitorDataMapEntity.builder()
                    .monitorId(monitorDataMap.getMonitorId())
                    .monitorName(monitorDataMap.getMonitorName())
                    .build());
        }
        return monitorDataMapEntities;
    }

    @Override
    public MonitorTreeConfigVO queryMonitorFlowData(String monitorId) {
       List<MonitorDataMapNode> monitorDataMapNodes = monitorDataMapNodeDao.queryMonitorNodeConfigByMonitorId(monitorId);
       List<MonitorDataMapNodeLink> monitorDataMapNodeLinks = monitorDataMapNodeLinkDao.queryMonitorNodeListConfigByMonitorId(monitorId);
       Map<String,List<String>> monitorNodeLinkMap =monitorDataMapNodeLinks.stream().collect(Collectors.groupingBy(MonitorDataMapNodeLink::getFromMonitorNodeId,Collectors.mapping(MonitorDataMapNodeLink::getToMonitorNodeId,Collectors.toList())));
       List<MonitorTreeConfigVO.Node> nodeList = new ArrayList<>(monitorDataMapNodes.size());
       for (MonitorDataMapNode monitorDataMapNode : monitorDataMapNodes){
           String cacheKey = Constants.RedisKey.monitor_node_data_count_key+monitorId+Constants.UNDERLINE+monitorDataMapNode.getMonitorNodeId();
           Long count = redisService.getAtomicLong(cacheKey);
           nodeList.add(MonitorTreeConfigVO.Node.builder()
                   .monitorNodeId(monitorDataMapNode.getMonitorNodeId())
                   .monitorNodeName(monitorDataMapNode.getMonitorNodeName())
                   .loc(monitorDataMapNode.getLoc())
                   .color(monitorDataMapNode.getColor())
                   .monitorNodeValue(null==count?"0":String.valueOf(count))
                   .build());

       }

       List<MonitorTreeConfigVO.Link> linkList = new ArrayList<>(monitorDataMapNodeLinks.size());
       for (MonitorDataMapNodeLink monitorDataMapNodeLink : monitorDataMapNodeLinks){
           //获取节点值
           String fromCacheKey = Constants.RedisKey.monitor_node_data_count_key+monitorId+Constants.UNDERLINE+monitorDataMapNodeLink.getFromMonitorNodeId();
           Long fromCacheCount = redisService.getAtomicLong(fromCacheKey);
           Long toCacheCount = 0L;
           //合并所有值
           List<String> toNodeIds = monitorNodeLinkMap.get(monitorDataMapNodeLink.getFromMonitorNodeId());
           for (String toNodeId : toNodeIds){
               String toCacheKey = Constants.RedisKey.monitor_node_data_count_key+monitorId+Constants.UNDERLINE+toNodeId;
               toCacheCount += redisService.getAtomicLong(toCacheKey);
           }
           long differenceValue =(null==fromCacheCount?0L:fromCacheCount)-toCacheCount;
            linkList.add(MonitorTreeConfigVO.Link.builder()
                    .fromMonitorNodeId(monitorDataMapNodeLink.getFromMonitorNodeId())
                    .toMonitorNodeId(monitorDataMapNodeLink.getToMonitorNodeId())
                    .linkKey(String.valueOf(monitorDataMapNodeLink.getId()))
                    .linkValue(String.valueOf(differenceValue <= 0 ? 0 : differenceValue))
                    .build()
            );

       }
       return MonitorTreeConfigVO.builder()
                .monitorId(monitorId)
                .nodeList(nodeList)
                .linkList(linkList)
                .build();
    }
}
