package com.dawang.monitor.trigger.http;

import com.dawang.monitor.domain.model.entity.MonitorDataEntity;
import com.dawang.monitor.domain.model.entity.MonitorDataMapEntity;
import com.dawang.monitor.domain.model.valobj.MonitorTreeConfigVO;
import com.dawang.monitor.domain.service.ILogAnalyticalService;
import com.dawang.monitor.trigger.http.dto.MonitorDataDTO;
import com.dawang.monitor.trigger.http.dto.MonitorDataMapDTO;
import com.dawang.monitor.trigger.http.dto.MonitorFlowDataDTO;
import com.dawang.monitor.types.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/monitor")
@CrossOrigin("*")

public class MonitorController {
    @Resource
    private ILogAnalyticalService logAnalyticalService;


    @RequestMapping(value = "/queryMonitorDataMapEntityList",method = {RequestMethod.GET})
    public Response<List<MonitorDataMapDTO>> queryMonitorDataMapEntityList(){
        try{
            List<MonitorDataMapEntity> monitorDataMapEntityList =logAnalyticalService.queryMonitorDataMapEntityList();
            List<MonitorDataMapDTO>  monitorDataMapDTOS = new ArrayList<>(monitorDataMapEntityList.size());
            for(MonitorDataMapEntity monitorDataMapEntity:monitorDataMapEntityList){
                monitorDataMapDTOS.add(MonitorDataMapDTO.builder()
                        .monitorId(monitorDataMapEntity.getMonitorId())
                        .monitorName(monitorDataMapEntity.getMonitorName())
                        .build());
            }
            return Response.<List<MonitorDataMapDTO>>builder()
                    .code("0000")
                    .info("调用成功")
                    .data(monitorDataMapDTOS)
                    .build();
        }catch (Exception e){
            return Response.<List<MonitorDataMapDTO>>builder()
                    .code("0001")
                    .info("调用监控列表失败")
                    .build();
        }
    }
    @RequestMapping(value = "/queryMonitorFlowMap",method = {RequestMethod.GET})
    public Response<MonitorFlowDataDTO> queryMonitorFlowMap(@RequestParam String monitorId){
        try{
            log.info("查询监控图数据，监控ID：{}",monitorId);
            MonitorTreeConfigVO monitorTreeConfigVO = logAnalyticalService.queryMonitorFlowData(monitorId);
            List<MonitorTreeConfigVO.Node> nodeList = monitorTreeConfigVO.getNodeList();
            List<MonitorTreeConfigVO.Link> linkList = monitorTreeConfigVO.getLinkList();

            List<MonitorFlowDataDTO.NodeData> nodeDataList = new ArrayList<>(nodeList.size());
            for(MonitorTreeConfigVO.Node node:nodeList){
                nodeDataList.add( new MonitorFlowDataDTO.NodeData(
                        node.getMonitorNodeId(),
                        node.getMonitorNodeId(),
                        node.getMonitorNodeName(),
                        node.getMonitorNodeValue(),
                        node.getLoc(),
                        node.getColor()
                ));
            }

            List<MonitorFlowDataDTO.LinkData> linkDataList = new ArrayList<>(linkList.size());

            for(MonitorTreeConfigVO.Link link:linkList){
                String linkValue = link.getLinkValue();
                linkDataList.add("0".equals(linkValue) ?
                        new MonitorFlowDataDTO.LinkData(link.getFromMonitorNodeId(), link.getToMonitorNodeId()) :
                        new MonitorFlowDataDTO.LinkData(link.getFromMonitorNodeId(), link.getToMonitorNodeId(), link.getLinkKey(), linkValue));
            }

            MonitorFlowDataDTO monitorFlowDataDTO = new MonitorFlowDataDTO();
            monitorFlowDataDTO.setNodeDataArray(nodeDataList);
            monitorFlowDataDTO.setLinkDataArray(linkDataList);

            return Response.<MonitorFlowDataDTO>builder()
                    .code("0000")
                    .info("调用成功")
                    .data(monitorFlowDataDTO)
                    .build();

        }
        catch (Exception e){
            log.error("查询监控图数据失败 minitorId:{}",monitorId,e);
            return Response.<MonitorFlowDataDTO>builder()
                    .code("0001")
                    .info("查询监控图数据失败")
                    .build();
        }
    }

    @RequestMapping(value = "query_monitor_data_list", method = RequestMethod.GET)
    public Response<List<MonitorDataDTO>> queryMonitorDataList(@RequestParam String monitorId,@RequestParam String monitorName,@RequestParam String monitorNodeId){
        try{
            log.info("查询监控数据，监控ID：{}",monitorId);
             List<MonitorDataEntity> monitorDataEntities=logAnalyticalService.queryMonitorDataEntityList(MonitorDataEntity.builder()
                     .monitorId(StringUtils.isBlank(monitorId.trim()) ? null : monitorId)
                     .monitorName(StringUtils.isBlank(monitorName.trim()) ? null : monitorName)
                     .monitorNodeId(StringUtils.isBlank(monitorNodeId.trim()) ? null : monitorNodeId)
                    .build()
            );

            List<MonitorDataDTO> monitorDataDTOS = new ArrayList<>();
            for (MonitorDataEntity monitorDataEntity : monitorDataEntities) {
                monitorDataDTOS.add(MonitorDataDTO.builder()
                        .monitorId(monitorDataEntity.getMonitorId())
                        .monitorName(monitorDataEntity.getMonitorName())
                        .monitorNodeId(monitorDataEntity.getMonitorNodeId())
                        .systemName(monitorDataEntity.getSystemName())
                        .clazzName(monitorDataEntity.getClazzName())
                        .methodName(monitorDataEntity.getMethodName())
                        .attributeName(monitorDataEntity.getAttributeName())
                        .attributeField(monitorDataEntity.getAttributeField())
                        .attributeValue(monitorDataEntity.getAttributeValue())
                        .build());
            }
            return Response.<List<MonitorDataDTO>>builder()
                    .code("0000")
                    .info("调用成功")
                    .data(monitorDataDTOS)
                    .build();


        }catch (Exception e){
            return Response.<List<MonitorDataDTO>>builder()
                    .code("0001")
                    .info("调用监控列表失败")
                    .build();
        }
    }
}
