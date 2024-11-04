package com.dawang.monitor.trigger.listener;

import com.alibaba.fastjson2.JSON;
import com.dawang.monitor.domain.service.ILogAnalyticalService;
import com.dawang.monitor.sdk.moudel.LogMessage;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.listener.MessageListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class MonitorLogListener implements MessageListener<LogMessage> {
    @Resource
    private ILogAnalyticalService logAnalyticalService;
    @Override
    public void onMessage(CharSequence charSequence, LogMessage logMessage) {
        try{
            log.info("监听监控日志信息,解析储存： {}", JSON.toJSONString(logMessage));
            logAnalyticalService.doAnalytical(logMessage.getSystemName(), logMessage.getClassName(), logMessage.getMethodName(), logMessage.getLogList());
        }catch (Exception e){
            log.error("监听监控日志信息,解析储存失败： {}", JSON.toJSONString(logMessage),e);
        }
    }
}
