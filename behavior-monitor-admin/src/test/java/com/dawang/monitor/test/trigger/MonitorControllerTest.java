package com.dawang.monitor.test.trigger;

import com.alibaba.fastjson.JSON;
import com.dawang.monitor.trigger.http.MonitorController;
import com.dawang.monitor.trigger.http.dto.MonitorDataDTO;
import com.dawang.monitor.trigger.http.dto.MonitorDataMapDTO;
import com.dawang.monitor.trigger.http.dto.MonitorFlowDataDTO;
import com.dawang.monitor.types.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@Slf4j
@RunWith(SpringRunner.class)
public class MonitorControllerTest {
    @Resource
    private MonitorController monitorController;

    @Test
    public void queryMonitorDataMapEntityList() {
        Response<List<MonitorDataMapDTO>> response = monitorController.queryMonitorDataMapEntityList();
        log.info("response:{}", JSON.toJSONString( response));
    }
@Test
    public  void test_queryMonitorFlowData(){
        Response<MonitorFlowDataDTO> response = monitorController.queryMonitorFlowMap("129009");
        log.info("response:{}", JSON.toJSONString( response));
    }

    @Test
    public void test_queryMonitorDataList(){
        Response<List<MonitorDataDTO>> response = monitorController.queryMonitorDataList("129009","","");
        log.info("测试结果: {}", com.alibaba.fastjson2.JSON.toJSONString(response));
    }

}
