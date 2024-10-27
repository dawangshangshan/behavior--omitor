package com.dawang.monitor.sdk.push;

import com.dawang.monitor.sdk.moudel.LogMessage;

public interface IPush {

    void open(String host, String port);

    void sendMessage(LogMessage logMessage);
}
