package com.dawang.monitor.trigger.http;

import com.dawang.monitor.domain.model.entity.MonitorDataMapEntity;
import com.dawang.monitor.domain.model.valobj.MonitorTreeConfigVO;
import com.dawang.monitor.domain.service.ILogAnalyticalService;
import com.dawang.monitor.trigger.http.dto.MonitorDataMapDTO;
import com.dawang.monitor.trigger.http.dto.MonitorFlowDataDTO;
import com.dawang.monitor.types.Response;
import lombok.extern.slf4j.Slf4j;
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
}
