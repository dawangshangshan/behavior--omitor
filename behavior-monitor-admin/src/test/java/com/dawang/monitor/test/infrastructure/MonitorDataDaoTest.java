package com.dawang.monitor.test.infrastructure;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.dawang.monitor.infrastructure.dao.IMonitorDataDao;
import com.dawang.monitor.infrastructure.po.MonitorData;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class MonitorDataDaoTest {

    @Resource
    private IMonitorDataDao monitorDataDao;

    @Before
    public void setUp() {
        if (monitorDataDao == null) {
            log.error("monitorDataDao 未正确注入");
        }
    }

    @Test
    public void test_queryMonitorDataList() {
        List<MonitorData> monitorData = monitorDataDao.queryMonitorDataList(new MonitorData());
        if (monitorData == null) {
            log.error("queryMonitorDataList 返回 null");
        }
        log.info("查询结果: {}", JSON.toJSONString(monitorData));
    }

}
