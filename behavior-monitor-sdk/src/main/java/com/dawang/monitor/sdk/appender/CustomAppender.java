package com.dawang.monitor.sdk.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.dawang.monitor.sdk.moudel.LogMessage;
import com.dawang.monitor.sdk.push.IPush;
import com.dawang.monitor.sdk.push.impl.RedisPush;

import java.util.Arrays;

public class CustomAppender<E> extends AppenderBase<E> {
    //系统名称
    private String systemName;
    //只采集指定范围的日志
    private String groupId;
    //redis 连接地址
    private String host;
    //连接端口
    private String port;
    private final IPush push = new RedisPush();
    @Override
    protected void append(E eventObjects) {
        //开启推送
        push.open(host,port);
        //获取日志
        if(eventObjects instanceof ILoggingEvent){
            ILoggingEvent event = (ILoggingEvent) eventObjects;
            String methodName = "unknown";
            String className= "unknown";
            StackTraceElement[] callerDataArray = event.getCallerData();
            if(null!=callerDataArray&&callerDataArray.length>0){
                StackTraceElement callerData = callerDataArray[0];
                methodName = callerData.getMethodName();
                className = callerData.getClassName();
            }
            if(!className.startsWith(groupId)){
                return ;
            }
            // 构建日志
            LogMessage logMessage = new LogMessage(systemName, className, methodName, Arrays.asList(event.getFormattedMessage().split(" ")));
            // 推送日志
            push.sendMessage(logMessage);

        }
    }

    public String getSystemName() {
        return systemName;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
