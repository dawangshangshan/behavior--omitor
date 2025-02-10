package com.dawang.monitor.test.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.dawang.monitor.infrastructure.dao.IMonitorDataDao;

import javax.annotation.Resource;

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



}
